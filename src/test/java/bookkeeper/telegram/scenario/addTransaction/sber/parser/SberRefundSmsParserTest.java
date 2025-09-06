package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SberRefundSmsParserTest {
    private final SberRefundSmsParser parser = new SberRefundSmsParser();

    @Test
    void parseOk_1() throws ParseException {
        var sms = parser.parse("MIR-1234 22:13 Отмена покупки 11р ЯндексGo Баланс: 1 217.51р");

        var referenceSms = new SberRefundSms();

        referenceSms.setRefundSum(new BigDecimal("11"));
        referenceSms.setRefundCurrency(Currency.getInstance("RUB"));
        referenceSms.setMerchant("ЯндексGo");
        referenceSms.setAccountBalance(new BigDecimal("1217.51"));
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));
        referenceSms.setAccountName("MIR-1234");

        assertEquals(referenceSms, sms);
    }

    @Test
    void parseOk_2() throws ParseException {
        var sms = parser.parse("MIR-1234 06.10.24 12:32 возврат покупки 1р Boosty Баланс: 1 540.42р");

        var referenceSms = new SberRefundSms();

        referenceSms.setRefundSum(new BigDecimal("1"));
        referenceSms.setRefundCurrency(Currency.getInstance("RUB"));
        referenceSms.setMerchant("Boosty");
        referenceSms.setAccountBalance(new BigDecimal("1540.42"));
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));
        referenceSms.setAccountName("MIR-1234");

        assertEquals(referenceSms, sms);
    }
}