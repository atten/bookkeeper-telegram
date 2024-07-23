package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffPurchaseSmsParser extends RegexpSpendingParser<TinkoffPurchaseSms> {

    public TinkoffPurchaseSmsParser() {
        super(
            TinkoffPurchaseSms.class,
            "Покупка, карта",
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
