package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

/**
 * Example:
 * СЧЁТ1234 18:26 перевод 1 000р Баланс: 3 879.81р
 */
@Data
public class SberTransferSms implements Spending {
    public String accountName;  // СЧЁТ1234
    public BigDecimal transferSum;  // 500
    public Currency transferCurrency;  // RUB
    public BigDecimal accountBalance;  // 653.04
    public Currency accountCurrency;  // RUB

    @Override
    public String getMerchant() {
        return "Перевод";
    }

    @Override
    public Optional<BigDecimal> getBalance() {
        return Optional.of(accountBalance);
    }
}
