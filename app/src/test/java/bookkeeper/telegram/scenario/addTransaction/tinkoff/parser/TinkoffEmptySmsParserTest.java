package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class TinkoffEmptySmsParserTest {
    private final TinkoffEmptySmsParser parser = new TinkoffEmptySmsParser();

    @Test
    void parseOk() throws ParseException {
        var sms = parser.parse("Никому не говорите код 1234! Вход в Тинькофф в 17:30 19.08.23");
        assertInstanceOf(TinkoffEmptySms.class, sms);
    }
}
