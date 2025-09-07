package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TinkoffDepositInterestSmsParserTest {
    private final TinkoffDepositInterestSmsParser parser = new TinkoffDepositInterestSmsParser();

    @Test
    void parseOk_1() throws ParseException {
        var sms = parser.parse("Выплата процентов по вкладу: 1 872.95 RUB");

        var referenceSms = new TinkoffDepositInterestSms();

        referenceSms.setInterestSum(new BigDecimal("1872.95"));
        referenceSms.setInterestCurrency(Currency.getInstance("RUB"));

        assertEquals(referenceSms, sms);
    }

    @Test
    void parseOk_2() throws ParseException {
        var sms = parser.parse("Зачислили проценты на вклад — 1 562,15 RUB: t.tb.ru/abcdef");

        var referenceSms = new TinkoffDepositInterestSms();

        referenceSms.setInterestSum(new BigDecimal("1562.15"));
        referenceSms.setInterestCurrency(Currency.getInstance("RUB"));

        assertEquals(referenceSms, sms);
    }
}