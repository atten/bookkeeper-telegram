package bookkeeper.telegram.scenario.viewAssets;

import bookkeeper.entity.Account;

import java.math.BigDecimal;
import java.util.Currency;

record Asset(Account account, BigDecimal balance, BigDecimal exchangeRate, Currency exchangeCurrency) {
    BigDecimal getExchangeBalance() {
        return balance.multiply(exchangeRate);
    }
}
