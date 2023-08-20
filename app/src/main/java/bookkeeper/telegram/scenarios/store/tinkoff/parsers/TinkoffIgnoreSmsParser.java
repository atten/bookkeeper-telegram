package bookkeeper.telegram.scenarios.store.tinkoff.parsers;

import bookkeeper.services.parsers.MarkSpendingParser;
import bookkeeper.services.parsers.SpendingParser;

import java.text.ParseException;
import java.util.List;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffIgnoreSmsParser implements SpendingParser<TinkoffIgnoreSms> {
    List<String> ignore = List.of(
        "Никому не говорите код",
        "Завтра выполним регулярный платеж",
        "Оцените решение вопроса",
        "Детали полета и маршрутная квитанция"
    );

    @Override
    public TinkoffIgnoreSms parse(String rawMessage) throws ParseException {
        ignore.stream().filter(rawMessage::contains).findAny().orElseThrow(() -> new ParseException(rawMessage, 0));
        return new TinkoffIgnoreSms();
    }
}
