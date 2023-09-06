package bookkeeper.telegram.scenarios.addTransactions.freehand.parsers;

import bookkeeper.services.parsers.Spending;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Example:
 * Еда 220
 */
@Data
public class FreehandRecord implements Spending {
    public String description;  // Еда
    public BigDecimal amount;  // 220

    @Override
    public String getMerchant() {
        return description;
    }
}
