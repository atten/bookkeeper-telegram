package bookkeeper.telegram.scenarios.addTransactions.freehand.parsers;

import bookkeeper.services.parsers.MarkSpendingParser;
import bookkeeper.services.parsers.SpendingParser;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

@MarkSpendingParser(provider = "freehand")
public class FreehandRecordParser implements SpendingParser<FreehandRecord> {

    @Override
    public FreehandRecord parse(String rawMessage) throws ParseException {
        String[] parts = rawMessage.split(" ");
        if (parts.length < 2)
            throw new ParseException(rawMessage, 0);

        var record = new FreehandRecord();
        record.description = parts[0];
        try {
            record.amount = new BigDecimal(parts[1]);
        } catch (NumberFormatException e) {
            throw new ParseException(parts[1], 0);
        }

        try {
            record.currency = Currency.getInstance(parts[2]);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            record.currency = null;
        }
        return record;
    }
}
