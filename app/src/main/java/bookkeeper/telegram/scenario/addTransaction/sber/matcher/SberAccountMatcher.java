package bookkeeper.telegram.scenario.addTransaction.sber.matcher;

import bookkeeper.entity.Account;
import bookkeeper.entity.TelegramUser;
import bookkeeper.service.matcher.AccountMatcher;
import bookkeeper.service.parser.Spending;
import bookkeeper.service.repository.AccountRepository;
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
        if (spending instanceof SberFpsPurchaseSms obj) {
            return Optional.of(getSberAccount(obj.getAccountName(), obj.getAccountCurrency(), user));
        }
        if (spending instanceof SberRecurringChargeSms obj) {
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
