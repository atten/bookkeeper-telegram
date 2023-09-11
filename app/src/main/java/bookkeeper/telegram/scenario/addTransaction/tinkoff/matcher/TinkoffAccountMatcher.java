package bookkeeper.telegram.scenario.addTransaction.tinkoff.matcher;

import bookkeeper.entity.Account;
import bookkeeper.entity.TelegramUser;
import bookkeeper.service.repository.AccountRepository;
import bookkeeper.service.matcher.AccountMatcher;
import bookkeeper.service.parser.Spending;
import bookkeeper.telegram.scenario.addTransaction.tinkoff.parser.*;

import java.util.Currency;
import java.util.Optional;

public class TinkoffAccountMatcher implements AccountMatcher {
    private final AccountRepository repository;

    public TinkoffAccountMatcher(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Account> match(Spending spending, TelegramUser user) {
        if (spending instanceof TinkoffPurchaseSms) {
            var obj = (TinkoffPurchaseSms) spending;
            return Optional.of(getTinkoffAccount(obj.accountCurrency, user));
        }
        if (spending instanceof TinkoffTransferSms) {
            var obj = (TinkoffTransferSms) spending;
            return Optional.of(getTinkoffAccount(obj.accountCurrency, user));
        }
        if (spending instanceof TinkoffRecurringChargeSms) {
            var obj = (TinkoffRecurringChargeSms) spending;
            return Optional.of(getTinkoffAccount(obj.chargeCurrency, user));
        }
        if (spending instanceof TinkoffFpsPurchaseSms) {
            var obj = (TinkoffFpsPurchaseSms) spending;
            return Optional.of(getTinkoffAccount(obj.accountCurrency, user));
        }
        if (spending instanceof TinkoffReplenishSms) {
            var obj = (TinkoffReplenishSms) spending;
            return Optional.of(getTinkoffAccount(obj.accountCurrency, user));
        }
        if (spending instanceof TinkoffIgnoreSms) {
            var defaultCurrency = Currency.getInstance("RUB");
            return Optional.of(getTinkoffAccount(defaultCurrency, user));
        }
        return Optional.empty();
    }

    private Account getTinkoffAccount(Currency currency, TelegramUser user) {
        return repository.getOrCreate(
            String.format("Tinkoff %s", currency.getCurrencyCode()),
            currency,
            user
        );
    }
}
