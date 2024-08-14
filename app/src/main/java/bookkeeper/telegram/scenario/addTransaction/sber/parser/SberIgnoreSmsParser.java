package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.SpendingParser;

import java.text.ParseException;
import java.util.List;

@MarkSpendingParser(provider = "sber")
public class SberIgnoreSmsParser implements SpendingParser<SberIgnoreSms> {
    private final List<String> ignoreContains = List.of(
        "изменён. Следующий перевод",
        "Никому не сообщайте код"
    );

    @Override
    public SberIgnoreSms parse(String rawMessage) throws ParseException {
        ignoreContains.stream().filter(rawMessage::contains).findAny().orElseThrow(() -> new ParseException(rawMessage, 0));
        return new SberIgnoreSms();
    }
}
