package bookkeeper.telegram.scenarios.addTransactions.freehand.parsers;

import bookkeeper.services.parsers.MarkSpendingParser;
import bookkeeper.services.parsers.SpendingParser;

import java.math.BigDecimal;
import java.text.ParseException;

@MarkSpendingParser(provider = "freehand")
public class FreehandRecordParser implements SpendingParser<FreehandRecord> {

    @Override
    public FreehandRecord parse(String rawMessage) throws ParseException {
        String[] parts = rawMessage.split(" ");
        if (parts.length < 2)
            throw new ParseException(rawMessage, 0);

        var strAmount = parts[parts.length - 1].replace("+", "-");
        var record = new FreehandRecord();
        record.description = rawMessage.substring(0, rawMessage.lastIndexOf(' '));
        try {
            record.amount = new BigDecimal(strAmount);
        } catch (NumberFormatException e) {
            throw new ParseException(rawMessage, 0);
        }

        return record;
    }
}
