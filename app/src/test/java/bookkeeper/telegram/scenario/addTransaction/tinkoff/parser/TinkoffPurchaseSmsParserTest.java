package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TinkoffPurchaseSmsParserTest {
    private final TinkoffPurchaseSmsParser parser = new TinkoffPurchaseSmsParser();

    @Test
    void parseOk() throws ParseException {
        var sms = parser.parse("Покупка, карта *0964. 621.8 RUB. VKUSVILL 2. Доступно 499.28 RUB");

        var referenceSms = new TinkoffPurchaseSms();

        referenceSms.cardIdentifier = "*0964";
        referenceSms.purchaseSum = new BigDecimal("621.8");
        referenceSms.purchaseCurrency = Currency.getInstance("RUB");
        referenceSms.merchant = "VKUSVILL 2";
        referenceSms.accountBalance = new BigDecimal("499.28");
        referenceSms.accountCurrency = Currency.getInstance("RUB");

        assertEquals(referenceSms, sms);
    }

    @Test
    void parseFail() {
        List<String> rawMessages = List.of(
            "Хер пойми что",
            "Покупка, карта *0964. 621.8 BTC. VKUSVILL 2. Доступно 499.28 RUB"
        );

        for (String rawMessage : rawMessages) {
            assertThrows(ParseException.class, () -> parser.parse(rawMessage));
        }
    }
}