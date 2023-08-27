package bookkeeper.telegram.scenarios.addTransactions.tinkoff.parsers;

import bookkeeper.services.parsers.Spending;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * Example:
 * Выполнен регулярный платеж "на мегафон" на 360 р.
 */
public class TinkoffRecurringChargeSms implements Spending {
    public BigDecimal chargeSum;  // 360
    public Currency chargeCurrency;  // RUB
    public String destination;  // на мегафон

    @Override
    public String getMerchant() {
        return destination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TinkoffRecurringChargeSms)) return false;
        TinkoffRecurringChargeSms that = (TinkoffRecurringChargeSms) o;
        return Objects.equals(chargeSum, that.chargeSum) && Objects.equals(chargeCurrency, that.chargeCurrency) && Objects.equals(destination, that.destination);
    }

    @Override
    public String toString() {
        return "TinkoffRecurringChargeSms{" +
                "chargeSum=" + chargeSum +
                ", chargeCurrency=" + chargeCurrency +
                ", destination='" + destination + '\'' +
                '}';
    }
}
