package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "sber")
public class SberFpsPurchaseSmsParser extends RegexpSpendingParser<SberFpsPurchaseSms> {
    public SberFpsPurchaseSmsParser() {
        super(
            SberFpsPurchaseSms.class,
            ACCOUNT_FIELD,
            TIME,
            "Покупка по СБП",
            AMOUNT_FIELD + CURRENCY_FIELD,
            TEXT_FIELD,
            "Баланс:",
            AMOUNT_FIELD + CURRENCY_FIELD
        );
    }
}
