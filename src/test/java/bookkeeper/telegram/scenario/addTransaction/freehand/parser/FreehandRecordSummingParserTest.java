package bookkeeper.telegram.scenario.addTransaction.freehand.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FreehandRecordSummingParserTest {
    private final FreehandRecordSummingParser parser = new FreehandRecordSummingParser();

    @Test
    void parseOk() throws ParseException {
        var record = parser.parse("еда 220+125.1+100");

        var referenceRecord = new FreehandRecord();

        referenceRecord.setDescription("еда");
        referenceRecord.setAmount(new BigDecimal("445.1"));

        assertEquals(referenceRecord, record);
    }
}