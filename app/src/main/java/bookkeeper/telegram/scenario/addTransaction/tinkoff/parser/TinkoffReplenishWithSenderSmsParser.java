package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffReplenishWithSenderSmsParser extends RegexpSpendingParser<TinkoffReplenishWithSenderSms> {

    public TinkoffReplenishWithSenderSmsParser() {
        super(
            TinkoffReplenishWithSenderSms.class,
            "Пополнение",
            "счет",
            TEXT,
            AMOUNT_FIELD,
            CURRENCY_FIELD,
            TEXT_FIELD,
            "Доступно",
            AMOUNT_FIELD,
            CURRENCY_FIELD
        );
    }
}
