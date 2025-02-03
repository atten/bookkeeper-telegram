package bookkeeper.telegram.scenario.addTransaction.freehand.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;


@MarkSpendingParser(provider = "freehand")
public class FreehandRecordParser extends RegexpSpendingParser<FreehandRecord> {
    public FreehandRecordParser() {
        super(
            FreehandRecord.class,
            TEXT_FIELD,
            AMOUNT_FIELD
        );
    }
}
