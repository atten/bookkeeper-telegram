package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SberReplenishSmsWithSenderParserTest {
    private final SberReplenishSmsWithSenderParser parser = new SberReplenishSmsWithSenderParser();

    @Test
    void parseOk_1() throws ParseException {
        var sms = parser.parse("СЧЁТ1234 00:21 Перевод 140р от Сергей С. Баланс: 879.81р");

        var referenceSms = new SberReplenishSmsWithSender();

        referenceSms.setReplenishSum(new BigDecimal("140"));
        referenceSms.setReplenishCurrency(Currency.getInstance("RUB"));
        referenceSms.setReplenishSender("Сергей С");
        referenceSms.setAccountBalance(new BigDecimal("879.81"));
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));
        referenceSms.setAccountName("СЧЁТ1234");

        assertEquals(referenceSms, sms);
    }

    @Test
    void parseOk_2() throws ParseException {
        var sms = parser.parse("MIR-1234 04:27 Перевод из Т‑Банк +1000р от ОЛЕГ В. Баланс: 1888.45р");

        var referenceSms = new SberReplenishSmsWithSender();

        referenceSms.setReplenishSum(new BigDecimal("1000"));
        referenceSms.setReplenishCurrency(Currency.getInstance("RUB"));
        referenceSms.setReplenishSender("ОЛЕГ В");
        referenceSms.setAccountBalance(new BigDecimal("1888.45"));
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));
        referenceSms.setAccountName("MIR-1234");

        assertEquals(referenceSms, sms);
    }
}