package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "sber")
public class SberPurchaseSmsParser extends RegexpSpendingParser<SberPurchaseSms> {
    public SberPurchaseSmsParser() {
        super(
            SberPurchaseSms.class,
            ACCOUNT_FIELD,
            TIME,
            "Покупка",
            AMOUNT_FIELD + CURRENCY_FIELD,
            TEXT_FIELD,
            "Баланс:",
            AMOUNT_FIELD + CURRENCY_FIELD
        );
    }
}
