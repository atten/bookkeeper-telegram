package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "sber")
public class SberPurchaseFeeSmsParser extends RegexpSpendingParser<SberPurchaseFeeSms> {
    public SberPurchaseFeeSmsParser() {
        super(
            SberPurchaseFeeSms.class,
            ACCOUNT_FIELD,
            TIME,
            nonCapturingGroup("Покупка", "Покупка по СБП", "Оплата", "Перевод"),
            AMOUNT_FIELD + CURRENCY_FIELD,
            nonCapturingGroup("комиссия"),
            AMOUNT_FIELD + CURRENCY_FIELD,
            TEXT_FIELD,
            "Баланс",
            AMOUNT_FIELD + CURRENCY_FIELD
        );
    }
}
