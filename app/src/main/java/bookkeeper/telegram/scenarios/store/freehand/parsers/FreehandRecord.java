package bookkeeper.telegram.scenarios.store.freehand.parsers;

import bookkeeper.services.parsers.Spending;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * Example:
 * Еда 220
 */
public class FreehandRecord implements Spending {
    public String description;  // Еда
    public BigDecimal amount;  // 220
    @Nullable
    public Currency currency;  // null

    @Override
    public String getMerchant() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FreehandRecord)) return false;
        FreehandRecord that = (FreehandRecord) o;
        return Objects.equals(description, that.description) && Objects.equals(amount, that.amount) && Objects.equals(currency, that.currency);
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
