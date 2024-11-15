package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffTransferSmsParser extends RegexpSpendingParser<TinkoffTransferSms> {

    public TinkoffTransferSmsParser() {
        super(
            TinkoffTransferSms.class,
            nonCapturingGroup("Платеж", "Перевод", "Покупка"),
            "Счет",
            TEXT,
            AMOUNT_FIELD,
            CURRENCY_FIELD,
            TEXT_FIELD,
            nonCapturingGroup("Баланс", "Доступно"),
            AMOUNT_FIELD,
            CURRENCY_FIELD
        );
    }
}
