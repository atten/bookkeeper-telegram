package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SberPurchaseForeignCurrencySmsParserTest {
    private final SberPurchaseForeignCurrencySmsParser parser = new SberPurchaseForeignCurrencySmsParser();

    @Test
    void parseOk() throws ParseException {
        var sms = parser.parse("MIR-1234 19:25 Покупка 6.50BYN (198.90р) YANDEX GO Баланс: 1 246.81р");

        var referenceSms = new SberPurchaseForeignCurrencySms();

        referenceSms.setAccountName("MIR-1234");
        referenceSms.setPurchaseSum(new BigDecimal("6.50"));
        referenceSms.setPurchaseCurrency(Currency.getInstance("BYN"));
        referenceSms.setPurchaseNativeSum(new BigDecimal("198.90"));
        referenceSms.setPurchaseNativeCurrency(Currency.getInstance("RUB"));
        referenceSms.setMerchant("YANDEX GO");
        referenceSms.setAccountBalance(new BigDecimal("1246.81"));
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));

        assertEquals(referenceSms, sms);
    }
}