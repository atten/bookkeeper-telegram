package bookkeeper.telegram.scenarios.addTransactions.tinkoff.parsers;

import bookkeeper.services.parsers.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Example:
 * Выполнен регулярный платеж "на мегафон" на 360 р.
 */
@Data
public class TinkoffRecurringChargeSms implements Spending {
    public BigDecimal chargeSum;  // 360
    public Currency chargeCurrency;  // RUB
    public String destination;  // на мегафон

    @Override
    public String getMerchant() {
        return destination;
    }
}
