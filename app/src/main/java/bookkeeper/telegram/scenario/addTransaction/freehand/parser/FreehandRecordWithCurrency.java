package bookkeeper.telegram.scenario.addTransaction.freehand.parser;

import bookkeeper.service.parser.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

/**
 * Example:
 * Еда 220 RUB
 */
@Data
public class FreehandRecordWithCurrency implements Spending {
    public String description;  // Еда
    public BigDecimal amount;  // 220
    public Currency currency;  // RUB

    @Override
    public String getMerchant() {
        return description;
    }

    @Override
    public Optional<BigDecimal> getBalance() {
        return Optional.empty();
    }
}
