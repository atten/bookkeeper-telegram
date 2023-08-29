package bookkeeper.telegram.scenarios.addTransactions.freehand.parsers;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class FreehandRecordWithCurrencyParserTest {
    private final FreehandRecordWithCurrencyParser parser = new FreehandRecordWithCurrencyParser();

    @Test
    void parseOk_1() throws ParseException {
        var record = parser.parse("еда 220 USD");

        var referenceRecord = new FreehandRecordWithCurrency();

        referenceRecord.description = "еда";
        referenceRecord.amount = new BigDecimal("220");
        referenceRecord.currency = Currency.getInstance("USD");

        assertEquals(referenceRecord, record);
    }

}