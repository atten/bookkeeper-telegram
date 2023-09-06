package bookkeeper.telegram.scenarios.addTransactions.freehand.parsers;

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
    public Currency currency;
}
