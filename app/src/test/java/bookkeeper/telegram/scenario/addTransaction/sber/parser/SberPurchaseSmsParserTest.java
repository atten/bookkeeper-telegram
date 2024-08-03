package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SberPurchaseSmsParserTest {
    private final SberPurchaseSmsParser parser = new SberPurchaseSmsParser();

    @Test
    void parseOk() throws ParseException {
        var sms = parser.parse("MIR-1234 16:00 Покупка 198р PEKARNYA Баланс: 1 681.81р");

        var referenceSms = new SberPurchaseSms();

        referenceSms.setAccountName("MIR-1234");
        referenceSms.setPurchaseSum(new BigDecimal("198"));
        referenceSms.setPurchaseCurrency(Currency.getInstance("RUB"));
        referenceSms.setMerchant("PEKARNYA");
        referenceSms.setAccountBalance(new BigDecimal("1681.81"));
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));

        assertEquals(referenceSms, sms);
    }

}