package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TinkoffWithdrawSmsParserTest {
    private final TinkoffWithdrawSmsParser parser = new TinkoffWithdrawSmsParser();

    @Test
    void parseOk() throws ParseException {
        var sms = parser.parse("Снятие, карта *0964. 3000 RUB. ATM 123. Доступно 343.32 RUB");

        var referenceSms = new TinkoffWithdrawalSms();

        referenceSms.cardIdentifier = "*0964";
        referenceSms.withdrawSum = new BigDecimal("3000");
        referenceSms.withdrawCurrency = Currency.getInstance("RUB");
        referenceSms.casher = "ATM 123";
        referenceSms.accountBalance = new BigDecimal("343.32");
        referenceSms.accountCurrency = Currency.getInstance("RUB");

        assertEquals(referenceSms, sms);
    }
}