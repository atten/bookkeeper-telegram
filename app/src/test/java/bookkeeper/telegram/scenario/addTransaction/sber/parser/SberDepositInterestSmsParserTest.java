package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SberDepositInterestSmsParserTest {
    private final SberDepositInterestSmsParser parser = new SberDepositInterestSmsParser();

    @Test
    void parseOk() throws ParseException {
        var sms = parser.parse("Накопит. счет Премьер *1234 Капитализация на 714,75р. Баланс: 1 714,75р. Подробнее s.sber.ru/ABCDE");

        var referenceSms = new SberDepositInterestSms();

        referenceSms.setAccountName("Накопит. счет Премьер");
        referenceSms.setAccountId("*1234");
        referenceSms.setInterestSum(new BigDecimal("714.75"));
        referenceSms.setInterestCurrency(Currency.getInstance("RUB"));
        referenceSms.setAccountBalance(new BigDecimal("1714.75"));
        referenceSms.setAccountCurrency(Currency.getInstance("RUB"));

        assertEquals(referenceSms, sms);
    }
}