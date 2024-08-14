package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

/**
 * Example:
 * MIR-1234 22:13 Отмена покупки 11р ЯндексGo Баланс: 1 217.51р
 */
@Data
public class SberRefundSms implements Spending {
    public String accountName;  // СЧЁТ1234
    public BigDecimal refundSum;  // 1000
    public Currency refundCurrency;  // RUB
    public String merchant;  // ЯндексGo
    public BigDecimal accountBalance;  // 1233.48
    public Currency accountCurrency;  // RUB

    @Override
    public String getMerchant() {
        return merchant;
    }

    @Override
    public Optional<BigDecimal> getBalance() {
        return Optional.of(accountBalance);
    }
}
