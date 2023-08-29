package bookkeeper.telegram.scenarios.addTransactions.freehand.parsers;

import bookkeeper.services.parsers.MarkSpendingParser;
import bookkeeper.services.parsers.SpendingParser;

import java.text.ParseException;
import java.util.Currency;

@MarkSpendingParser(provider = "freehand")
public class FreehandRecordWithCurrencyParser implements SpendingParser<FreehandRecordWithCurrency> {

    @Override
    public FreehandRecordWithCurrency parse(String rawMessage) throws ParseException {
        String[] parts = rawMessage.split(" ");
        if (parts.length < 3)
            throw new ParseException(rawMessage, 0);

        var rawMessageWithoutCurrency = rawMessage.substring(0, rawMessage.lastIndexOf(' '));
        var recordWithoutCurrency = new FreehandRecordParser().parse(rawMessageWithoutCurrency);

        var record = new FreehandRecordWithCurrency();

        record.amount = recordWithoutCurrency.amount;
        record.description = recordWithoutCurrency.description;

        try {
            record.currency = Currency.getInstance(parts[parts.length - 1].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ParseException(String.format("Cannot parse currency: %s", e), 0);
        }
        return record;
    }
}
