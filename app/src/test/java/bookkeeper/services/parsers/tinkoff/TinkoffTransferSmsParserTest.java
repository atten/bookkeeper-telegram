package bookkeeper.services.parsers.tinkoff;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TinkoffTransferSmsParserTest {
    private final TinkoffTransferSmsParser parser = new TinkoffTransferSmsParser();

    @Test
    void parseOk() throws ParseException {
        var sms = parser.parse("Перевод. Счет RUB. 500 RUB. Сергей С. Баланс 653.04 RUB");

        var referenceSms = new TinkoffTransferSms();

        referenceSms.transferSum = new BigDecimal("500");
        referenceSms.transferCurrency = Currency.getInstance("RUB");
        referenceSms.destination = "Сергей С";
        referenceSms.accountBalance = new BigDecimal("653.04");
        referenceSms.accountCurrency = Currency.getInstance("RUB");

        assertEquals(referenceSms, sms);
    }

    @Test
    void parseFail() {
        List<String> rawMessages = List.of(
            "Хер пойми что",
            "Перевод. Счет RUB. 500 BTC. Сергей С. Баланс 653.04 RUB"
        );

        for (String rawMessage : rawMessages) {
            assertThrows(ParseException.class, () -> parser.parse(rawMessage));
        }
    }
}