package bookkeeper.telegram.scenarios.save.tinkoff.parsers;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class TinkoffReplenishSmsParserTest {
    private final TinkoffReplenishSmsParser parser = new TinkoffReplenishSmsParser();

    @Test
    void parseOk_1() throws ParseException {
        var sms = parser.parse("Пополнение, счет RUB. 236 RUB. Доступно 713.79 RUB");

        var referenceSms = new TinkoffReplenishSms();

        referenceSms.replenishSum = new BigDecimal("236");
        referenceSms.replenishCurrency = Currency.getInstance("RUB");
        referenceSms.accountCurrency = Currency.getInstance("RUB");
        referenceSms.accountBalance = new BigDecimal("713.79");

        assertEquals(referenceSms, sms);
    }

    @Test
    void parseOk_2() throws ParseException {
        var sms = parser.parse("Возврат. Счет RUB. 2070 RUB. Доступно 3000 RUB");

        var referenceSms = new TinkoffReplenishSms();

        referenceSms.replenishSum = new BigDecimal("2070");
        referenceSms.replenishCurrency = Currency.getInstance("RUB");
        referenceSms.accountCurrency = Currency.getInstance("RUB");
        referenceSms.accountBalance = new BigDecimal("3000");

        assertEquals(referenceSms, sms);
    }
}