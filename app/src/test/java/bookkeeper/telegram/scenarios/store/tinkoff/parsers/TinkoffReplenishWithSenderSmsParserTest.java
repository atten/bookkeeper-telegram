package bookkeeper.telegram.scenarios.store.tinkoff.parsers;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class TinkoffReplenishWithSenderSmsParserTest {
    private final TinkoffReplenishWithSenderSmsParser parser = new TinkoffReplenishWithSenderSmsParser();

    @Test
    void parseOk() throws ParseException {
        var sms = parser.parse("Пополнение, счет RUB. 236 RUB. Сергей С. Доступно 713.79 RUB");

        var referenceSms = new TinkoffReplenishWithSenderSms();

        referenceSms.replenishSender = "Сергей С";
        referenceSms.replenishSum = new BigDecimal("236");
        referenceSms.replenishCurrency = Currency.getInstance("RUB");
        referenceSms.accountCurrency = Currency.getInstance("RUB");
        referenceSms.accountBalance = new BigDecimal("713.79");

        assertEquals(referenceSms, sms);
    }
}