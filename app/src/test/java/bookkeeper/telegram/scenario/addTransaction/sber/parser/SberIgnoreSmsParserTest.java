package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class SberIgnoreSmsParserTest {
    private final SberIgnoreSmsParser parser = new SberIgnoreSmsParser();

    @Test
    void parseOk() throws ParseException {
        var sms = parser.parse("Автоперевод «ABC» со счёта *1234 клиенту Иван Пупкин Б. на 100р изменён. Следующий перевод 01.09.24.");
        assertInstanceOf(SberIgnoreSms.class, sms);
    }
}
