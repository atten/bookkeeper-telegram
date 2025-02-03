package bookkeeper.telegram.scenario.addTransaction.freehand.parser;

import bookkeeper.service.parser.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Optional;

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

    @Override
    public Optional<BigDecimal> getBalance() {
        return Optional.empty();
    }
}
