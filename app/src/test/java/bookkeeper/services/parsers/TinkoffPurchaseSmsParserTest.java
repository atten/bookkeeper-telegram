package bookkeeper.services.parsers;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class TinkoffPurchaseSmsParserTest {

    @Test
    void parse() throws ParseException {
        TinkoffPurchaseSmsParser parser = new TinkoffPurchaseSmsParser();
        TinkoffPurchaseSms sms = parser.parse("Покупка, карта *0964. 621.8 RUB. VKUSVILL 2. Доступно 499.28 RUB");

        TinkoffPurchaseSms referenceSms = new TinkoffPurchaseSms();

        referenceSms.cardIdentifier = "*0964";
        referenceSms.purchaseSum = new BigDecimal("621.8");
        referenceSms.purchaseCurrency = Currency.getInstance("RUB");
        referenceSms.merchantTag = "VKUSVILL 2";
        referenceSms.accountBalance = new BigDecimal("499.28");
        referenceSms.accountCurrency = Currency.getInstance("RUB");

        assertEquals(referenceSms, sms);
    }
}