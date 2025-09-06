package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SberPurchaseFeeSmsParserTest {
    private final SberPurchaseFeeSmsParser parser = new SberPurchaseFeeSmsParser();

    @Test
    void parseOk() throws ParseException {
        var sms = parser.parse("СЧЁТ1234 16:40 Оплата 223р Комиссия 22.23р АО ПЦ Баланс: 360.73р");

        var referenceSms = new SberPurchaseFeeSms();

        referenceSms.setAccountName("СЧЁТ1234");
        referenceSms.setPurchaseSum(new BigDecimal("223"));
        referenceSms.setPurchaseCurrency(Currency.getInstance("RUB"));
        referenceSms.setFeeSum(new BigDecimal("22.23"));
        referenceSms.setFeeCurrency(Currency.getInstance("RUB"));
        referenceSms.setMerchant("АО ПЦ");
        referenceSms.setAccountBalance(new BigDecimal("360.73"));
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));

        assertEquals(referenceSms, sms);
    }
}