package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class SberTransferSmsParserTest {
    private final SberTransferSmsParser parser = new SberTransferSmsParser();

    @Test
    void parseOk_1() throws ParseException {
        var sms = parser.parse("СЧЁТ1234 18:26 перевод 1 000р Баланс: 3 879.81р");

        var referenceSms = new SberTransferSms();

        referenceSms.setAccountName("СЧЁТ1234");
        referenceSms.setTransferSum(new BigDecimal("1000"));
        referenceSms.setTransferCurrency(Currency.getInstance("RUB"));
        referenceSms.setAccountBalance(new BigDecimal("3879.81"));
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));

        assertEquals(referenceSms, sms);
    }
}