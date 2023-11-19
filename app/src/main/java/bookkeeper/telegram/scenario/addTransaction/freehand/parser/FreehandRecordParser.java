package bookkeeper.telegram.scenario.addTransaction.freehand.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.SpendingParser;

import java.math.BigDecimal;
import java.text.ParseException;

@MarkSpendingParser(provider = "freehand")
public class FreehandRecordParser implements SpendingParser<FreehandRecord> {

    @Override
    public FreehandRecord parse(String rawMessage) throws ParseException {
        String[] parts = rawMessage.split(" ");
        if (parts.length < 2)
            throw new ParseException(rawMessage, 0);

        // Treat '+100' amount as negative spending (double inversion means refill).
        // Replace comma (localized amount format) with dot (normal BigDecimal format).
        var strAmount = parts[parts.length - 1]
            .replace("+", "-")
            .replace(",", ".");

        var record = new FreehandRecord();
        record.setDescription(rawMessage.substring(0, rawMessage.lastIndexOf(' ')));
        try {
            record.setAmount(new BigDecimal(strAmount));
        } catch (NumberFormatException e) {
            throw new ParseException(rawMessage, 0);
        }

        return record;
    }
}
