package bookkeeper.telegram.scenario.addTransaction.freehand.parser;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Currency;

/**
 * Example:
 * Еда 220 RUB
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FreehandRecordWithCurrency extends FreehandRecord {
    private Currency currency;
}
