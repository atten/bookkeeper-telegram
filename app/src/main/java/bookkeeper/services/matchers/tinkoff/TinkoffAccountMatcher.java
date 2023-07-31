package bookkeeper.services.matchers.tinkoff;

import bookkeeper.entities.Account;
import bookkeeper.entities.TelegramUser;
import bookkeeper.repositories.AccountRepository;
import bookkeeper.services.matchers.AccountMatcher;
import bookkeeper.services.parsers.Spending;
import bookkeeper.services.parsers.tinkoff.TinkoffPurchaseSms;
import org.jetbrains.annotations.Nullable;

public class TinkoffAccountMatcher implements AccountMatcher {
    final AccountRepository repository;

    public TinkoffAccountMatcher(AccountRepository repository) {
        this.repository = repository;
    }

    @Nullable
    @Override
    public Account match(Spending spending, TelegramUser user) {
        if (spending instanceof TinkoffPurchaseSms) {
            var currency = ((TinkoffPurchaseSms) spending).accountCurrency;
            return repository.getOrCreate(
                String.format("Tinkoff %s", currency.getCurrencyCode()),
                currency,
                user
            );
        }
        return null;
    }
}
