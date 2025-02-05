package bookkeeper.telegram.scenario.addTransaction.tinkoff.matcher;

import bookkeeper.dao.AccountRepository;
import bookkeeper.dao.entity.Account;
import bookkeeper.dao.entity.TelegramUser;
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
        if (spending instanceof TinkoffPurchaseSms obj) {
            return Optional.of(getTinkoffAccount(obj.accountCurrency, user));
        }
        if (spending instanceof TinkoffPurchaseSmsWithDate obj) {
            return Optional.of(getTinkoffAccount(obj.accountCurrency, user));
        }
        if (spending instanceof TinkoffTransferSms obj) {
            return Optional.of(getTinkoffAccount(obj.accountCurrency, user));
        }
        if (spending instanceof TinkoffRecurringChargeSms obj) {
            return Optional.of(getTinkoffAccount(obj.chargeCurrency, user));
        }
        if (spending instanceof TinkoffFpsPurchaseSms obj) {
            return Optional.of(getTinkoffAccount(obj.accountCurrency, user));
        }
        if (spending instanceof TinkoffReplenishSms obj) {
            return Optional.of(getTinkoffAccount(obj.accountCurrency, user));
        }
        if (spending instanceof TinkoffReplenishWithSenderSms obj) {
            return Optional.of(getTinkoffAccount(obj.accountCurrency, user));
        }
        if (spending instanceof TinkoffDepositInterestSms obj) {
            return Optional.of(getTinkoffAccount(obj.getInterestCurrency(), user));
        }
        if (spending instanceof TinkoffWithdrawalSms obj) {
            return Optional.of(getTinkoffAccount(obj.getAccountCurrency(), user));
        }
        if (spending instanceof TinkoffEmptySms) {
            var defaultCurrency = Currency.getInstance("RUB");
            return Optional.of(getTinkoffAccount(defaultCurrency, user));
        }
        return Optional.empty();
    }

    private Account getTinkoffAccount(Currency currency, TelegramUser user) {
        return repository.getMatchOrCreate(
            "Tinkoff %s".formatted(currency.getCurrencyCode()),
            currency,
            user
        );
    }
}
