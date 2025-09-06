package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SberReplenishSmsParserTest {
    private final SberReplenishSmsParser parser = new SberReplenishSmsParser();

    @Test
    void parseOk_1() throws ParseException {
        var sms = parser.parse("СЧЁТ1234 16:36 Зачисление 1 000р Баланс: 1 123.48р");

        var referenceSms = new SberReplenishSms();

        referenceSms.setReplenishSum(new BigDecimal("1000"));
        referenceSms.setReplenishCurrency(Currency.getInstance("RUB"));
        referenceSms.setAccountBalance(new BigDecimal("1123.48"));
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));
        referenceSms.setAccountName("СЧЁТ1234");

        assertEquals(referenceSms, sms);
    }

    @Test
    void parseOk_2() throws ParseException {
        var sms = parser.parse("MIR-1234 20:55 зачисление 47.33р Баланс: 681.81р");

        var referenceSms = new SberReplenishSms();

        referenceSms.setReplenishSum(new BigDecimal("47.33"));
        referenceSms.setReplenishCurrency(Currency.getInstance("RUB"));
        referenceSms.setAccountBalance(new BigDecimal("681.81"));
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));
        referenceSms.setAccountName("MIR-1234");

        assertEquals(referenceSms, sms);
    }

    @Test
    void parseOk_3() throws ParseException {
        var sms = parser.parse("СЧЁТ1234 13:09 Зачисление зарплаты 1 000р Баланс: 1 103р");

        var referenceSms = new SberReplenishSms();

        referenceSms.setReplenishSum(new BigDecimal("1000"));
        referenceSms.setReplenishCurrency(Currency.getInstance("RUB"));
        referenceSms.setAccountBalance(new BigDecimal("1103"));
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));
        referenceSms.setAccountName("СЧЁТ1234");

        assertEquals(referenceSms, sms);
    }

}