package bookkeeper.telegram.scenarios.addTransactions.freehand.matchers;

import bookkeeper.entities.Account;
import bookkeeper.entities.AccountTransaction;
import bookkeeper.entities.TelegramUser;
import bookkeeper.services.matchers.AccountMatcher;
import bookkeeper.services.parsers.Spending;
import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.repositories.AccountTransactionRepository;
import bookkeeper.telegram.scenarios.addTransactions.freehand.parsers.FreehandRecord;
import bookkeeper.telegram.scenarios.addTransactions.freehand.parsers.FreehandRecordWithCurrency;

import java.util.Comparator;
import java.util.Currency;
import java.util.Optional;

public class FreehandAccountMatcher implements AccountMatcher {
    private final AccountRepository repository;
    private final AccountTransactionRepository transactionRepository;

    public FreehandAccountMatcher(AccountRepository repository, AccountTransactionRepository transactionRepository) {
        this.repository = repository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Optional<Account> match(Spending spending, TelegramUser user) {
        if (spending instanceof FreehandRecordWithCurrency obj) {
            return Optional.of(getLastUsedAccount(user, obj.currency));
        }
        if (spending instanceof FreehandRecord) {
            return Optional.of(getLastUsedAccount(user));
        }
        return Optional.empty();
    }

    /**
     * Find account of most recent user transaction. Create RUB account as a fallback.
     */
    private Account getLastUsedAccount(TelegramUser user) {
        var accounts = repository.filter(user);

        return accounts.stream()
            .map(account -> transactionRepository.findRecent(user, account.getCurrency(), 1))
            .filter(transactions -> !transactions.isEmpty())
            .map(transactions -> transactions.get(0))
            .max(Comparator.comparing(AccountTransaction::getTimestamp))
            .map(AccountTransaction::getAccount)
            .orElseGet(() -> repository.getOrCreate(
                "Счёт RUB",
                Currency.getInstance("RUB"),
                user
            ));
    }

    /**
     * Find account of most recent user transaction is specified currency. Create one as a fallback.
     */
    private Account getLastUsedAccount(TelegramUser user, Currency currency) {
        var recentTransactions = transactionRepository.findRecent(user, currency, 1);
        if (!recentTransactions.isEmpty())
            return recentTransactions.get(0).getAccount();

        var accounts = repository.filter(user);
        return accounts.stream()
            .filter(account -> account.getCurrency() == currency)
            .findFirst()
            .orElseGet(() -> repository.getOrCreate(
                String.format("Счёт %s", currency.getCurrencyCode()),
                currency,
                user
        ));
    }
}