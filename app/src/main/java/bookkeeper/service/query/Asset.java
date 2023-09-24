package bookkeeper.service.query;

import bookkeeper.entity.Account;

import java.math.BigDecimal;
import java.util.Currency;

public record Asset(Account account, BigDecimal balance, BigDecimal exchangeRate, Currency exchangeCurrency) {
    public BigDecimal getExchangeBalance() {
        return balance.multiply(exchangeRate);
    }

    public boolean isEmpty() {
        return balance.stripTrailingZeros().equals(BigDecimal.ZERO);
    }
}
