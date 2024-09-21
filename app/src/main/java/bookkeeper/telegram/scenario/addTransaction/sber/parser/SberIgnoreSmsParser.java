package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.SpendingParser;

import java.text.ParseException;
import java.util.List;

@MarkSpendingParser(provider = "sber")
public class SberIgnoreSmsParser implements SpendingParser<SberIgnoreSms> {
    private final List<String> ignoreIfContains = List.of(
        "изменён. Следующий перевод",
        "Никому не сообщайте код",
        "Никому его не сообщайте",
        "Вход в Сбер",
        "Первый платёж со счёта",
        "» изменён",
        "Выплата кешбэка по оплате через СБП"
    );

    @Override
    public SberIgnoreSms parse(String rawMessage) throws ParseException {
        ignoreIfContains.stream().filter(rawMessage::contains).findAny().orElseThrow(() -> new ParseException(rawMessage, 0));
        return new SberIgnoreSms();
    }
}
