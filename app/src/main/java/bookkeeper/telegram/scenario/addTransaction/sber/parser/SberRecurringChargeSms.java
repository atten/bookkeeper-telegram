package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

/**
 * Example:
 * СЧЁТ1234 01:27 Оплата 70р за уведомления по СберКартам. Следующее списание 23.08.24. Баланс 2 611,81р
 */
@Data
public class SberRecurringChargeSms implements Spending {
    private String accountName;  // СЧЁТ1234
    private BigDecimal chargeSum;  // 70
    private Currency chargeCurrency;  // RUB
    private String destination;  // за уведомления по СберКартам
    private BigDecimal accountBalance;  // 2611.81
    private Currency accountCurrency;  // RUB

    @Override
    public String getMerchant() {
        return destination;
    }

    @Override
    public Optional<BigDecimal> getBalance() {
        return Optional.of(accountBalance);
    }
}
