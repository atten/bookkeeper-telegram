package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

/**
 * Example:
 * СЧЁТ1234 00:21 Перевод 140р от Сергей С. Баланс: 879.81р
 */
@Data
public class SberReplenishSmsWithSender implements Spending {
    public String accountName;  // СЧЁТ1234
    public BigDecimal replenishSum;  // 1000
    public Currency replenishCurrency;  // RUB
    public String replenishSender;  // Сергей С
    public BigDecimal accountBalance;  // 1233.48
    public Currency accountCurrency;  // RUB

    @Override
    public String getMerchant() {
        return replenishSender;
    }

    @Override
    public Optional<BigDecimal> getBalance() {
        return Optional.of(accountBalance);
    }
}
