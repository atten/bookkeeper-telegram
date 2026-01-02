package bookkeeper.telegram.scenario.addTransaction.freehand.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FreehandRecordWithCurrencyParserTest {
    private final FreehandRecordWithCurrencyParser parser = new FreehandRecordWithCurrencyParser();

    @Test
    void parseOk_1() throws ParseException {
        var record = parser.parse("еда 220 USD");

        var referenceRecord = new FreehandRecordWithCurrency();

        referenceRecord.setDescription("еда");
        referenceRecord.setAmount(new BigDecimal("220"));
        referenceRecord.setCurrency(Currency.getInstance("USD"));

        assertEquals(referenceRecord, record);
    }

    @Test
    void parseOk_2() throws ParseException {
        var record = parser.parse("еда 220р");

        var referenceRecord = new FreehandRecordWithCurrency();

        referenceRecord.setDescription("еда");
        referenceRecord.setAmount(new BigDecimal("220"));
        referenceRecord.setCurrency(Currency.getInstance("RUB"));

        assertEquals(referenceRecord, record);
    }

}