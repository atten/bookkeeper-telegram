package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class SberReplenishSmsWithSenderParserTest {
    private final SberReplenishSmsWithSenderParser parser = new SberReplenishSmsWithSenderParser();

    @Test
    void parseOk_1() throws ParseException {
        var sms = parser.parse("СЧЁТ1234 00:21 Перевод 140р от Сергей С. Баланс: 879.81р");

        var referenceSms = new SberReplenishSmsWithSender();

        referenceSms.setReplenishSum(new BigDecimal("140"));
        referenceSms.setReplenishCurrency(Currency.getInstance("RUB"));
        referenceSms.setAccountBalance(new BigDecimal("879.81"));
        referenceSms.setReplenishSender("Сергей С");
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));
        referenceSms.setAccountName("СЧЁТ1234");

        assertEquals(referenceSms, sms);
    }
}