package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.SpendingParser;

import java.text.ParseException;
import java.util.List;

@MarkSpendingParser(provider = "sber")
public class SberEmptySmsParser implements SpendingParser<SberEmptySms> {
    private final List<String> ignoreIfContains = List.of(
        "изменён. Следующий перевод",
        "не исполнен по техническим причинам",
        "Никому не сообщайте код",
        "Никому его не сообщайте",
        "Вход в Сбер",
        "Первый платёж со счёта",
        "Счёт на оплату",
        "» изменён",
        "Выплата кешбэка по оплате через СБП",
        "Участник программы «СберСпасибо» перевёл вам",
        "Спасибо. Проверить баланс",
        "Спасибо. Посмотреть баланс",
        "доступно больше привилегий"
    );

    @Override
    public SberEmptySms parse(String rawMessage) throws ParseException {
        ignoreIfContains.stream().filter(rawMessage::contains).findAny().orElseThrow(() -> new ParseException(rawMessage, 0));
        return new SberEmptySms();
    }
}
