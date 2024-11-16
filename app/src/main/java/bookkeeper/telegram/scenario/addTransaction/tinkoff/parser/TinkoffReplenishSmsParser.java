package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffReplenishSmsParser extends RegexpSpendingParser<TinkoffReplenishSms> {

    public TinkoffReplenishSmsParser() {
        super(
            TinkoffReplenishSms.class,
            nonCapturingGroup("Пополнение", "Возврат", "Возврат СБП"),
            "счет",
            TEXT,
            AMOUNT_FIELD,
            CURRENCY_FIELD,
            "Доступно",
            AMOUNT_FIELD,
            CURRENCY_FIELD
        );
    }
}
