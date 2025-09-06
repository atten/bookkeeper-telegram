package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

/**
 * Example:
 * СЧЁТ1234 01:27 Оплата 70р за уведомления по СберКартам. Следующее списание 23.08.24. Баланс 2 611,81р
 * СЧЁТ1234 09:58 Оплата 550р Автоплатёж Энторнет Баланс: 1 378.52р
 */
@Data
public class SberRecurringChargeSms implements Spending {
    public String accountName;  // СЧЁТ1234
    public BigDecimal chargeSum;  // 70
    public Currency chargeCurrency;  // RUB
    public String destination;  // за уведомления по СберКартам
    public BigDecimal accountBalance;  // 2611.81
    public Currency accountCurrency;  // RUB

    @Override
    public String getMerchant() {
        return destination;
    }

    @Override
    public Optional<BigDecimal> getBalance() {
        return Optional.of(accountBalance);
    }
}
