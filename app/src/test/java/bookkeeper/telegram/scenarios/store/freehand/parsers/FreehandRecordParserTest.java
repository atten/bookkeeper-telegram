package bookkeeper.telegram.scenarios.store.freehand.parsers;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class FreehandRecordParserTest {
    private final FreehandRecordParser parser = new FreehandRecordParser();

    @Test
    void parseOk_1() throws ParseException {
        var record = parser.parse("еда 220");

        var referenceRecord = new FreehandRecord();

        referenceRecord.description = "еда";
        referenceRecord.amount = new BigDecimal("220");
        referenceRecord.currency = null;

        assertEquals(referenceRecord, record);
    }

    @Test
    void parseOk_2() throws ParseException {
        var record = parser.parse("еда 220 USD");

        var referenceRecord = new FreehandRecord();

        referenceRecord.description = "еда";
        referenceRecord.amount = new BigDecimal("220");
        referenceRecord.currency = Currency.getInstance("USD");

        assertEquals(referenceRecord, record);
    }
}