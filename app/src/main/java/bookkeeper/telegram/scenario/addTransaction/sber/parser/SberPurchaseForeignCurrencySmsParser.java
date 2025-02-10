package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "sber")
public class SberPurchaseForeignCurrencySmsParser extends RegexpSpendingParser<SberPurchaseForeignCurrencySms> {
    public SberPurchaseForeignCurrencySmsParser() {
        super(
            SberPurchaseForeignCurrencySms.class,
            ACCOUNT_FIELD,
            TIME,
            nonCapturingGroup("Покупка", "Покупка по СБП", "Оплата", "Перевод"),
            AMOUNT_FIELD + CURRENCY_FIELD,
            AMOUNT_FIELD + CURRENCY_FIELD,
            TEXT_FIELD,
            "Баланс",
            AMOUNT_FIELD + CURRENCY_FIELD
        );
    }
}
