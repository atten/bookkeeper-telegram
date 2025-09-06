package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffRecurringChargeSmsParser extends RegexpSpendingParser<TinkoffRecurringChargeSms> {
    public TinkoffRecurringChargeSmsParser() {
        super(
            TinkoffRecurringChargeSms.class,
            nonCapturingGroup("Выполнен регулярный платеж", "Выполнен автоплатеж"),
            TEXT_FIELD,
            "на",
            AMOUNT_FIELD,
            CURRENCY_FIELD
        );
    }
}
