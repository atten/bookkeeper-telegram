package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SberPurchaseSmsParserTest {
    private final SberPurchaseSmsParser parser = new SberPurchaseSmsParser();

    @Test
    void parseOk_1() throws ParseException {
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

    @Test
    void parseOk_2() throws ParseException {
        var sms = parser.parse("MIR-1234 20:55 Покупка по СБП 733.52р Прием платежей mos Баланс: 2 634.48р");

        var referenceSms = new SberPurchaseSms();

        referenceSms.setAccountName("MIR-1234");
        referenceSms.setPurchaseSum(new BigDecimal("733.52"));
        referenceSms.setPurchaseCurrency(Currency.getInstance("RUB"));
        referenceSms.setMerchant("Прием платежей mos");
        referenceSms.setAccountBalance(new BigDecimal("2634.48"));
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));

        assertEquals(referenceSms, sms);
    }

    @Test
    void parseOk_3() throws ParseException {
        var sms = parser.parse("СЧЁТ1234 19:03 Оплата 550р OPLATA USLUG MOS Баланс: 1 394.21р");

        var referenceSms = new SberPurchaseSms();

        referenceSms.setAccountName("СЧЁТ1234");
        referenceSms.setPurchaseSum(new BigDecimal("550"));
        referenceSms.setPurchaseCurrency(Currency.getInstance("RUB"));
        referenceSms.setMerchant("OPLATA USLUG MOS");
        referenceSms.setAccountBalance(new BigDecimal("1394.21"));
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));

        assertEquals(referenceSms, sms);
    }

    @Test
    void parseOk_4() throws ParseException {
        var sms = parser.parse("СЧЁТ1234 09:58 Оплата 550р Автоплатёж Энторнет Баланс: 1 378.52р");

        var referenceSms = new SberPurchaseSms();

        referenceSms.setAccountName("СЧЁТ1234");
        referenceSms.setPurchaseSum(new BigDecimal("550"));
        referenceSms.setPurchaseCurrency(Currency.getInstance("RUB"));
        referenceSms.setMerchant("Автоплатёж Энторнет");
        referenceSms.setAccountBalance(new BigDecimal("1378.52"));
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));

        assertEquals(referenceSms, sms);
    }

    @Test
    void parseOk_5() throws ParseException {
        var sms = parser.parse("MIR-1234 12:57 перевод 593р Т-Банк Баланс: 1 891.76р");

        var referenceSms = new SberPurchaseSms();

        referenceSms.setAccountName("MIR-1234");
        referenceSms.setPurchaseSum(new BigDecimal("593"));
        referenceSms.setPurchaseCurrency(Currency.getInstance("RUB"));
        referenceSms.setMerchant("Т-Банк");
        referenceSms.setAccountBalance(new BigDecimal("1891.76"));
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));

        assertEquals(referenceSms, sms);
    }
}