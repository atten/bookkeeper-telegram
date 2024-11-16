package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffPurchaseSmsWithDateParser extends RegexpSpendingParser<TinkoffPurchaseSmsWithDate> {
    public TinkoffPurchaseSmsWithDateParser() {
        super(
            TinkoffPurchaseSmsWithDate.class,
            "Покупка",
            DATE_FIELD,
            "карта",
            TEXT_FIELD,
            AMOUNT_FIELD,
            CURRENCY_FIELD,
            TEXT_FIELD,
            "Доступно",
            AMOUNT_FIELD,
            CURRENCY_FIELD
        );
    }
}
