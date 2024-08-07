package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

/**
 * Example:
 * СЧЁТ1234 16:36 Зачисление 1 000р Баланс: 1 123.48р
 */
@Data
public class SberReplenishSms implements Spending {
    public String accountName;  // СЧЁТ1234
    public BigDecimal replenishSum;  // 1000
    public Currency replenishCurrency;  // RUB
    public BigDecimal accountBalance;  // 1233.48
    public Currency accountCurrency;  // RUB

    @Override
    public String getMerchant() {
        return "Зачисление";
    }

    @Override
    public Optional<BigDecimal> getBalance() {
        return Optional.of(accountBalance);
    }
}
