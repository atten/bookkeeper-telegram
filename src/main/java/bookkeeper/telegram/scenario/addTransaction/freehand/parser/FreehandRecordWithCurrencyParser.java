package bookkeeper.telegram.scenario.addTransaction.freehand.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "freehand")
public class FreehandRecordWithCurrencyParser extends RegexpSpendingParser<FreehandRecordWithCurrency> {
    public FreehandRecordWithCurrencyParser() {
        super(
            FreehandRecordWithCurrency.class,
            TEXT_FIELD,
            AMOUNT_FIELD + CURRENCY_FIELD
        );
    }
}
