package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SberReplenishSmsByFpsParserTest {
    private final SberReplenishSmsByFpsParser parser = new SberReplenishSmsByFpsParser();

    @Test
    void parseOk_1() throws ParseException {
        var sms = parser.parse("MIR-1234 11:36 Выплата кешбэка по СБП 65.54р CSHBCK_АО НСПК_В2С Баланс: 2 138.44р");

        var referenceSms = new SberReplenishSms();

        referenceSms.setReplenishSum(new BigDecimal("65.54"));
        referenceSms.setReplenishCurrency(Currency.getInstance("RUB"));
        referenceSms.setAccountBalance(new BigDecimal("2138.44"));
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));
        referenceSms.setAccountName("MIR-1234");

        assertEquals(referenceSms, sms);
    }

}