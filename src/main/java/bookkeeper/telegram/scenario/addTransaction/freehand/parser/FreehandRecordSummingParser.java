package bookkeeper.telegram.scenario.addTransaction.freehand.parser;

import bookkeeper.service.parser.MarkSpendingParser;

import java.math.BigDecimal;
import java.text.ParseException;


@MarkSpendingParser(provider = "freehand")
public class FreehandRecordSummingParser extends FreehandRecordParser {
    @Override
    protected BigDecimal parseAmountField(String amount) throws ParseException {
        BigDecimal result = BigDecimal.ZERO;
        for (String part : amount.split("\\+")) {
            result = result.add(super.parseAmountField(part));
        }
        return result;
    }

    @Override
    public int weight() {
        // prefer inherited parser (if applicable) because it handles more corner cases
        return super.weight() - 1;
    }
}
