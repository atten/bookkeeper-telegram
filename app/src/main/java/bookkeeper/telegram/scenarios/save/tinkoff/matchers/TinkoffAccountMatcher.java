package bookkeeper.telegram.scenarios.save.tinkoff.matchers;

import bookkeeper.entities.Account;
import bookkeeper.entities.TelegramUser;
import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.matchers.AccountMatcher;
import bookkeeper.services.parsers.Spending;
import bookkeeper.telegram.scenarios.save.tinkoff.parsers.*;
import org.jetbrains.annotations.Nullable;

import java.util.Currency;

public class TinkoffAccountMatcher implements AccountMatcher {
    private final AccountRepository repository;

    public TinkoffAccountMatcher(AccountRepository repository) {
        this.repository = repository;
    }

    @Nullable
    @Override
    public Account match(Spending spending, TelegramUser user) {
        if (spending instanceof TinkoffPurchaseSms) {
            var obj = (TinkoffPurchaseSms) spending;
            return getTinkoffAccount(obj.accountCurrency, user);
        }
        if (spending instanceof TinkoffTransferSms) {
            var obj = (TinkoffTransferSms) spending;
            return getTinkoffAccount(obj.accountCurrency, user);
        }
        if (spending instanceof TinkoffRecurringChargeSms) {
            var obj = (TinkoffRecurringChargeSms) spending;
            return getTinkoffAccount(obj.chargeCurrency, user);
        }
        if (spending instanceof TinkoffFpsPurchaseSms) {
            var obj = (TinkoffFpsPurchaseSms) spending;
            return getTinkoffAccount(obj.accountCurrency, user);
        }
        if (spending instanceof TinkoffReplenishSms) {
            var obj = (TinkoffReplenishSms) spending;
            return getTinkoffAccount(obj.accountCurrency, user);
        }
        if (spending instanceof TinkoffIgnoreSms) {
            var defaultCurrency = Currency.getInstance("RUB");
            return getTinkoffAccount(defaultCurrency, user);
        }
        return null;
    }

    private Account getTinkoffAccount(Currency currency, TelegramUser user) {
        return repository.getOrCreate(
            String.format("Tinkoff %s", currency.getCurrencyCode()),
            currency,
            user
        );
    }
}
