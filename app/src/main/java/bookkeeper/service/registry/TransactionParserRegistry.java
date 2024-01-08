package bookkeeper.service.registry;

import bookkeeper.entity.Account;
import bookkeeper.entity.AccountTransaction;
import bookkeeper.entity.TelegramUser;
import bookkeeper.enums.Expenditure;
import bookkeeper.service.matcher.AccountMatcher;
import bookkeeper.service.matcher.AmountMatcher;
import bookkeeper.service.matcher.ExpenditureMatcher;
import bookkeeper.service.matcher.TimestampMatcher;
import bookkeeper.service.parser.Spending;
import bookkeeper.service.parser.SpendingParserRegistry;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionParserRegistry {
    private SpendingParserRegistry spendingParserRegistry = new SpendingParserRegistry();
    private final ArrayList<AccountMatcher> accountMatchers = new ArrayList<>();
    private final ArrayList<AmountMatcher> amountMatchers = new ArrayList<>();
    private final ArrayList<ExpenditureMatcher> expenditureMatchers = new ArrayList<>();
    private final ArrayList<TimestampMatcher> timestampMatchers = new ArrayList<>();

    public record TransactionParseResult(Optional<AccountTransaction> transaction, Optional<ParseException> error, String rawMessage) {}

    public TransactionParserRegistry addAccountMatcher(AccountMatcher matcher) {
        accountMatchers.add(matcher);
        return this;
    }

    public TransactionParserRegistry addAmountMatcher(AmountMatcher matcher) {
        amountMatchers.add(matcher);
        return this;
    }

    public TransactionParserRegistry addTimestampMatcher(TimestampMatcher matcher) {
        timestampMatchers.add(matcher);
        return this;
    }

    public TransactionParserRegistry setSpendingParserRegistry(SpendingParserRegistry registry) {
        this.spendingParserRegistry = registry;
        return this;
    }

    public TransactionParserRegistry addExpenditureMatcher(ExpenditureMatcher matcher) {
        expenditureMatchers.add(matcher);
        return this;
    }

    public AccountTransaction parse(String rawMessage, TelegramUser user) throws ParseException {
        var spending = spendingParserRegistry.parse(rawMessage);
        var transaction = new AccountTransaction();

        transaction.setAccount(matchAccount(spending, user));
        transaction.setAmount(matchAmount(spending));
        transaction.setExpenditure(matchExpenditure(spending, user));
        transaction.setTimestamp(matchTimestamp(spending));
        transaction.setRaw(rawMessage);
        transaction.setCreatedAt(Instant.now());

        return transaction;
    }

    public List<TransactionParseResult> parseMultiple(String[] rawMessages, TelegramUser user) {
        List<TransactionParseResult> results = new ArrayList<>();
        var now = Instant.now();

        for (var message : rawMessages ) {
            AccountTransaction transaction = null;
            ParseException error = null;
            try {
                transaction = parse(message, user);
            } catch (ParseException e) {
                error = e;
            }

            if (transaction != null) {
                // transactions within same batch must have same creation timestamp for further filtering
                transaction.setCreatedAt(now);
            }

            var result = new TransactionParseResult(Optional.ofNullable(transaction), Optional.ofNullable(error), message);
            results.add(result);
        }

        return results;
    }

    /**
     * First matched account is preferred.
     */
    private Account matchAccount(Spending spending, TelegramUser user) {
        return accountMatchers
                .stream()
                .map(matcher -> matcher.match(spending, user))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow();
    }

    /**
     * First matched amount is preferred.
     */
    private BigDecimal matchAmount(Spending spending) {
        return amountMatchers
                .stream()
                .map(matcher -> matcher.match(spending))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow();
    }

    /**
     * First expenditureMatcher which returns any category different from "Other" is preferred.
     */
    private Expenditure matchExpenditure(Spending spending, TelegramUser user) {
        return expenditureMatchers
                .stream()
                .map(matcher -> matcher.match(spending, user))
                .filter(expenditure -> expenditure != Expenditure.OTHER)
                .findFirst()
                .orElse(Expenditure.OTHER);
    }

    /**
     * First matched timestamp is preferred (default = now()).
     */
    private Instant matchTimestamp(Spending spending) {
        return timestampMatchers
                .stream()
                .map(matcher -> matcher.match(spending))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElse(Instant.now());
    }
}
