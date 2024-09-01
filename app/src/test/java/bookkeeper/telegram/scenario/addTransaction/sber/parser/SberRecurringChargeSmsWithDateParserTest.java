package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SberRecurringChargeSmsWithDateParserTest {
    private final SberRecurringChargeSmsWithDateParser parser = new SberRecurringChargeSmsWithDateParser();

    @Test
    void parseOk() throws ParseException {
        var sms = parser.parse("СЧЁТ1234 01:27 Оплата 70р за уведомления по СберКартам. Следующее списание 23.08.24. Баланс 2 611,81р");

        var referenceSms = new SberRecurringChargeSms();

        referenceSms.setAccountName("СЧЁТ1234");
        referenceSms.setChargeSum(new BigDecimal("70"));
        referenceSms.setChargeCurrency(Currency.getInstance("RUB"));
        referenceSms.setDestination("за уведомления по СберКартам");
        referenceSms.setAccountBalance(new BigDecimal("2611.81"));
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));

        assertEquals(referenceSms, sms);
    }
}
