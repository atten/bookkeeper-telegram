package bookkeeper.telegram.scenario.addTransaction.freehand.matcher;

import bookkeeper.dao.AccountRepository;
import bookkeeper.dao.AccountTransactionRepository;
import bookkeeper.dao.entity.Account;
import bookkeeper.dao.entity.AccountTransaction;
import bookkeeper.dao.entity.TelegramUser;
import bookkeeper.service.matcher.AccountMatcher;
import bookkeeper.service.parser.Spending;
import bookkeeper.telegram.scenario.addTransaction.freehand.parser.FreehandRecord;
import bookkeeper.telegram.scenario.addTransaction.freehand.parser.FreehandRecordWithCurrency;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FreehandAccountMatcher implements AccountMatcher {
    private final AccountRepository repository;
    private final AccountTransactionRepository transactionRepository;

    public FreehandAccountMatcher(AccountRepository repository, AccountTransactionRepository transactionRepository) {
        this.repository = repository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Find most recent user account or create one as a fallback.
     */
    @Override
    public Optional<Account> match(Spending spending, TelegramUser user) {
        if (spending instanceof FreehandRecordWithCurrency obj) {
            var accounts = repository.filter(user, obj.getCurrency());
            var account = getLastUsedAccount(accounts)
                .orElseGet(() -> getOrCreateAccount(user, obj.getCurrency()));
            return Optional.of(account);
        }
        if (spending instanceof FreehandRecord) {
            var accounts = repository.filter(user);
            var account = getLastUsedAccount(accounts)
                .orElseGet(() -> getOrCreateAccount(user, Currency.getInstance("RUB")));
            return Optional.of(account);
        }
        return Optional.empty();
    }

    /**
     * Find account among provided with the biggest count of recently added transactions.
     */
    private Optional<Account> getLastUsedAccount(Collection<Account> accounts) {
        return transactionRepository.findRecentAdded(accounts, 3)
            .stream()
            .map(AccountTransaction::getAccount)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet()
            .stream()
            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
            .map(Map.Entry::getKey).findFirst();
    }

    private Account getOrCreateAccount(TelegramUser user, Currency currency) {
        return repository.getMatchOrCreate(
            "Счёт %s".formatted(currency.getCurrencyCode()),
            currency,
            user
        );
    }
}
