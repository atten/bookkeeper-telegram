package bookkeeper.telegram.scenarios.store.tinkoff.parsers;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class TinkoffIgnoreSmsParserTest {
    TinkoffIgnoreSmsParser parser = new TinkoffIgnoreSmsParser();

    @Test
    void parseOk() throws ParseException {
        var sms = parser.parse("Никому не говорите код 1234! Вход в Тинькофф в 17:30 19.08.23");
        assertInstanceOf(TinkoffIgnoreSms.class, sms);
    }

}