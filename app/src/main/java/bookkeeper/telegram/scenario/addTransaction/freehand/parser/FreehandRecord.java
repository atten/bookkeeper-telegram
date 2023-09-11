package bookkeeper.telegram.scenario.addTransaction.freehand.parser;

import bookkeeper.service.parser.Spending;
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
