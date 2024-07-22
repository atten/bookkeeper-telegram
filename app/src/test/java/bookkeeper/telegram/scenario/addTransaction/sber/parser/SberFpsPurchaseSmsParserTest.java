package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class SberFpsPurchaseSmsParserTest {
    private final SberFpsPurchaseSmsParser parser = new SberFpsPurchaseSmsParser();

    @Test
    void parseOk() throws ParseException {
        var sms = parser.parse("MIR-1234 20:55 Покупка по СБП 733.52р Прием платежей mos Баланс: 2 634.48р");

        var referenceSms = new SberFpsPurchaseSms();

        referenceSms.setAccountName("MIR-1234");
        referenceSms.setPurchaseSum(new BigDecimal("733.52"));
        referenceSms.setPurchaseCurrency(Currency.getInstance("RUB"));
        referenceSms.setMerchant("Прием платежей mos");
        referenceSms.setAccountBalance(new BigDecimal("2634.48"));
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));

        assertEquals(referenceSms, sms);
    }

}