package bookkeeper.telegram.scenarios.addTransactions.freehand.parsers;

import java.util.Currency;
import java.util.Objects;

/**
 * Example:
 * Еда 220 RUB
 */
public class FreehandRecordWithCurrency extends FreehandRecord {
    public Currency currency;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FreehandRecordWithCurrency)) return false;
        if (!super.equals(o)) return false;
        FreehandRecordWithCurrency that = (FreehandRecordWithCurrency) o;
        return Objects.equals(currency, that.currency);
    }

    @Override
    public String toString() {
        return "FreehandRecord{" +
                "description='" + description + '\'' +
                ", amount=" + amount +
                ", currency=" + currency +
                '}';
    }
}
