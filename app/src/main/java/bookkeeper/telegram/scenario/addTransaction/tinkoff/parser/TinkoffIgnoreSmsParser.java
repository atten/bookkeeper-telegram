package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.SpendingParser;

import java.text.ParseException;
import java.util.List;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffIgnoreSmsParser implements SpendingParser<TinkoffIgnoreSms> {
    private final List<String> ignore = List.of(
        "Никому не говорите код",
        "Завтра выполним регулярный платеж",
        "Оцените решение вопроса",
        "Детали полета и маршрутная квитанция",
        "Полис путешественника оплачен и доступен",
        "Отказ"
    );

    @Override
    public TinkoffIgnoreSms parse(String rawMessage) throws ParseException {
        ignore.stream().filter(rawMessage::contains).findAny().orElseThrow(() -> new ParseException(rawMessage, 0));
        return new TinkoffIgnoreSms();
    }
}
