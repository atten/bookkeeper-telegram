package bookkeeper.telegram.scenario.addTransaction.freehand.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.SpendingParser;

import java.text.ParseException;
import java.util.Currency;

@MarkSpendingParser(provider = "freehand")
public class FreehandRecordWithCurrencyParser implements SpendingParser<FreehandRecordWithCurrency> {

    @Override
    public FreehandRecordWithCurrency parse(String rawMessage) throws ParseException {
        String[] parts = rawMessage.split(" ");
        // don't accept too long messages (not to clash with other parsers)
        if (parts.length < 3 || parts.length > 6)
            throw new ParseException(rawMessage, 0);

        var rawMessageWithoutCurrency = rawMessage.substring(0, rawMessage.lastIndexOf(' '));
        var recordWithoutCurrency = new FreehandRecordParser().parse(rawMessageWithoutCurrency);

        var record = new FreehandRecordWithCurrency();

        record.setAmount(recordWithoutCurrency.getAmount());
        record.setDescription(recordWithoutCurrency.getDescription());

        try {
            record.setCurrency(Currency.getInstance(parts[parts.length - 1].toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ParseException("Cannot parse currency: %s".formatted(e), 0);
        }
        return record;
    }
}
