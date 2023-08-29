package bookkeeper.telegram.scenarios.addTransactions.freehand.parsers;

import bookkeeper.services.parsers.Spending;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Example:
 * Еда 220
 */
public class FreehandRecord implements Spending {
    public String description;  // Еда
    public BigDecimal amount;  // 220

    @Override
    public String getMerchant() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FreehandRecord)) return false;
        FreehandRecord that = (FreehandRecord) o;
        return Objects.equals(description, that.description) && Objects.equals(amount, that.amount);
    }

    @Override
    public String toString() {
        return "FreehandRecord{" +
                "description='" + description + '\'' +
                ", amount=" + amount +
                '}';
    }
}
