package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.SpendingParser;

import java.text.ParseException;
import java.util.List;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffEmptySmsParser implements SpendingParser<TinkoffEmptySms> {
    private final List<String> ignore = List.of(
        "Никому не говорите код",
        "Завтра выполним",
        "Посмотрите ответ",
        "Оцените решение вопроса",
        "Детали полета",
        "По вашему рейсу",
        "Изменилось расписание рейса",
        "Полис путешественника",
        "Посоветуйте",
        "Советуйте",
        "Дарим",
        "Отказ",
        "Вход в",
        "Вклад"
    );

    @Override
    public TinkoffEmptySms parse(String rawMessage) throws ParseException {
        ignore.stream().filter(rawMessage::startsWith).findAny().orElseThrow(() -> new ParseException(rawMessage, 0));
        return new TinkoffEmptySms();
    }
}
