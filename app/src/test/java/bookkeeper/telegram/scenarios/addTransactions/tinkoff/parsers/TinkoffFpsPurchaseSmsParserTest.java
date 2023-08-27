package bookkeeper.telegram.scenarios.addTransactions.tinkoff.parsers;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TinkoffFpsPurchaseSmsParserTest {
    private final TinkoffFpsPurchaseSmsParser parser = new TinkoffFpsPurchaseSmsParser();

    @Test
    void parseOk() throws ParseException {
        var sms = parser.parse("Оплата СБП, счет RUB. 1760 RUB. YANDEX.AFISHA. Доступно 694.79 RUB");

        var referenceSms = new TinkoffFpsPurchaseSms();

        referenceSms.purchaseSum = new BigDecimal("1760");
        referenceSms.purchaseCurrency = Currency.getInstance("RUB");
        referenceSms.merchant = "YANDEX.AFISHA";
        referenceSms.accountBalance = new BigDecimal("694.79");
        referenceSms.accountCurrency = Currency.getInstance("RUB");

        assertEquals(referenceSms, sms);
    }
}