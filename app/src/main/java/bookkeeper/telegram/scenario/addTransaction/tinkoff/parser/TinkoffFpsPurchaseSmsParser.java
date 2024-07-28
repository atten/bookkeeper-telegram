package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffFpsPurchaseSmsParser extends RegexpSpendingParser<TinkoffFpsPurchaseSms> {
    public TinkoffFpsPurchaseSmsParser() {
        super(
            TinkoffFpsPurchaseSms.class,
            "Оплата СБП, счет",
            OPTIONAL_TEXT,
            AMOUNT_FIELD,
            CURRENCY_FIELD,
            TEXT_FIELD,
            "Доступно",
            AMOUNT_FIELD,
            CURRENCY_FIELD
        );
    }
}
