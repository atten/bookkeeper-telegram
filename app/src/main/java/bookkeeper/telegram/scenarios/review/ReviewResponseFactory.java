package bookkeeper.telegram.scenarios.review;

import bookkeeper.entities.TelegramUser;
import bookkeeper.enums.Expenditure;
import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.repositories.AccountTransactionRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class ReviewResponseFactory {
    private final AccountRepository accountRepository;
    private final AccountTransactionRepository transactionRepository;

    public ReviewResponseFactory(AccountRepository accountRepository, AccountTransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public String getMonthlyExpenses(TelegramUser user, int monthDelta) {
        var lines = new ArrayList<String>();
        var creditByCurrency = new HashMap<Currency, BigDecimal>();
        var debitByCurrency = new HashMap<Currency, BigDecimal>();
        var allByCurrency = new HashMap<Currency, BigDecimal>();
        var accounts = accountRepository.findForUser(user);
        var maxExpenditureLength = Expenditure.enabledValues().stream().map(expenditure -> expenditure.getVerboseName().length()).max(Comparator.naturalOrder()).orElse(0);
        var formatString = "%-" + maxExpenditureLength + "s %s";  // example: "%-15s %s"

        for (var account : accounts) {
            var currency = account.getCurrency();

            creditByCurrency.putIfAbsent(currency, BigDecimal.ZERO);
            debitByCurrency.putIfAbsent(currency, BigDecimal.ZERO);
            allByCurrency.putIfAbsent(currency, BigDecimal.ZERO);

            lines.add(String.format("*%s*", account.getName()));
            lines.add("```");

            for (var expenditure : Expenditure.enabledValues()) {
                var amount = transactionRepository.getAggregatedAmount(account, expenditure, monthDelta);
                var sign = amount.compareTo(BigDecimal.ZERO);

                allByCurrency.merge(currency, amount, BigDecimal::add);

                if (sign > 0) {
                    creditByCurrency.merge(currency, amount, BigDecimal::add);
                } else if (sign < 0) {
                    debitByCurrency.merge(currency, amount, BigDecimal::add);
                } else {
                    continue;
                }

                lines.add(String.format(formatString, expenditure.getVerboseName(), roundedAmountString(amount)));
            }

            lines.add("```");
        }

        lines.add("*Всего*");
        lines.add("```");
        lines.add(String.format("%-7s %s", "Расходы", amountByCurrencyString(creditByCurrency)));
        lines.add(String.format("%-7s %s", "Доходы", amountByCurrencyString(debitByCurrency)));
        lines.add(String.format("%-7s %s", "Баланс", amountByCurrencyString(allByCurrency)));
        lines.add("```");

        return String.join("\n", lines);
    }

    private String amountByCurrencyString(Map<Currency, BigDecimal> values) {
        return values
            .entrySet().stream()
            .map(i -> roundedAmountString(i.getValue()) + " " + i.getKey().getSymbol())
            .collect(Collectors.joining(", "));
    }

    private String roundedAmountString(BigDecimal value) {
        return String.format("% ,.0f", value).replace("-", "+");
    }
}
