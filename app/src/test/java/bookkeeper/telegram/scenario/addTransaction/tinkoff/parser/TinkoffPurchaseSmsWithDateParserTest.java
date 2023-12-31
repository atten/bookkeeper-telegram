package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class TinkoffPurchaseSmsWithDateParserTest {

    @Test
    void parse() throws ParseException {
        var parser = new TinkoffPurchaseSmsWithDateParser();
        var sms = parser.parse("Покупка 19.07.2023. Карта *0964. 56 RUB. MOS.TRANSP. Доступно 499.28 RUB");

        var referenceSms = new TinkoffPurchaseSmsWithDate();

        referenceSms.purchaseDate = LocalDate.of(2023, Month.JULY, 19);
        referenceSms.cardIdentifier = "*0964";
        referenceSms.purchaseSum = new BigDecimal("56");
        referenceSms.purchaseCurrency = Currency.getInstance("RUB");
        referenceSms.merchant = "MOS.TRANSP";
        referenceSms.accountBalance = new BigDecimal("499.28");
        referenceSms.accountCurrency = Currency.getInstance("RUB");

        assertEquals(referenceSms, sms);
    }

    @Test
    void parseDateAnotherPattern() throws ParseException {
        var parser = new TinkoffPurchaseSmsWithDateParser();
        var sms = parser.parse("Покупка 9.07.2023. Карта *0964. 56 RUB. MOS.TRANSP. Доступно 499.28 RUB");
        var expectedDate = LocalDate.of(2023, Month.JULY, 9);
        assertEquals(expectedDate, sms.purchaseDate);
    }
}