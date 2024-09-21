package bookkeeper.telegram.scenario.addTransaction.sber.matcher;

import bookkeeper.dao.AccountRepository;
import bookkeeper.dao.entity.Account;
import bookkeeper.dao.entity.TelegramUser;
import bookkeeper.service.matcher.AccountMatcher;
import bookkeeper.service.parser.Spending;
import bookkeeper.telegram.scenario.addTransaction.sber.parser.*;

import java.util.Currency;
import java.util.Optional;

public class SberAccountMatcher implements AccountMatcher {
    private final AccountRepository repository;

    public SberAccountMatcher(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Account> match(Spending spending, TelegramUser user) {
        if (spending instanceof SberPurchaseSms obj) {
            return Optional.of(getSberAccount(obj.getAccountName(), obj.getAccountCurrency(), user));
        }
        if (spending instanceof SberRecurringChargeSms obj) {
            return Optional.of(getSberAccount(obj.getAccountName(), obj.getAccountCurrency(), user));
        }
        if (spending instanceof SberRefundSms obj) {
            return Optional.of(getSberAccount(obj.getAccountName(), obj.getAccountCurrency(), user));
        }
        if (spending instanceof SberReplenishSms obj) {
            return Optional.of(getSberAccount(obj.getAccountName(), obj.getAccountCurrency(), user));
        }
        if (spending instanceof SberReplenishSmsWithSender obj) {
            return Optional.of(getSberAccount(obj.getAccountName(), obj.getAccountCurrency(), user));
        }
        if (spending instanceof SberTransferSms obj) {
            return Optional.of(getSberAccount(obj.getAccountName(), obj.getAccountCurrency(), user));
        }
        if (spending instanceof SberDepositInterestSms obj) {
            return Optional.of(getSberAccount(obj.getAccountId(), obj.getAccountCurrency(), user));
        }
        if (spending instanceof SberIgnoreSms) {
            var defaultCurrency = Currency.getInstance("RUB");
            var defaultAccountName = "Sber";
            return Optional.of(getSberAccount(defaultAccountName, defaultCurrency, user));
        }
        return Optional.empty();
    }

    private Account getSberAccount(String name, Currency currency, TelegramUser user) {
        return repository.getMatchOrCreate(
            name,
            currency,
            user
        );
    }
}
