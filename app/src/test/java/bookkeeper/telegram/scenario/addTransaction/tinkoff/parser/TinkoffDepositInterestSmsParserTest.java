package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TinkoffDepositInterestSmsParserTest {
    private final TinkoffDepositInterestSmsParser parser = new TinkoffDepositInterestSmsParser();


    @Test
    void parseOk() throws ParseException {
        var sms = parser.parse("Выплата процентов по вкладу: 1 872.95 RUB");

        var referenceSms = new TinkoffDepositInterestSms();

        referenceSms.interestSum = new BigDecimal("1872.95");
        referenceSms.interestCurrency = Currency.getInstance("RUB");

        assertEquals(referenceSms, sms);

    }
}