package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TinkoffRecurringChargeSmsParserTest {
    private final TinkoffRecurringChargeSmsParser parser = new TinkoffRecurringChargeSmsParser();

    @Test
    void parseOk_1() throws ParseException {
        var sms = parser.parse("Выполнен регулярный платеж \"на мегафон\" на 360 р.");

        var referenceSms = new TinkoffRecurringChargeSms();

        referenceSms.chargeSum = new BigDecimal("360");
        referenceSms.chargeCurrency = Currency.getInstance("RUB");
        referenceSms.destination = "на мегафон";

        assertEquals(referenceSms, sms);
    }

    @Test
    void parseOk_2() throws ParseException {
        var sms = parser.parse("Выполнен регулярный платеж \"ЖКХ\" на 1 000 р.");

        var referenceSms = new TinkoffRecurringChargeSms();

        referenceSms.chargeSum = new BigDecimal("1000");
        referenceSms.chargeCurrency = Currency.getInstance("RUB");
        referenceSms.destination = "ЖКХ";

        assertEquals(referenceSms, sms);
    }

    @Test
    void parseOk_3() throws ParseException {
        var sms = parser.parse("Выполнен автоплатеж «на мегафон» на 360 р.");

        var referenceSms = new TinkoffRecurringChargeSms();

        referenceSms.chargeSum = new BigDecimal("360");
        referenceSms.chargeCurrency = Currency.getInstance("RUB");
        referenceSms.destination = "на мегафон";

        assertEquals(referenceSms, sms);
    }
}