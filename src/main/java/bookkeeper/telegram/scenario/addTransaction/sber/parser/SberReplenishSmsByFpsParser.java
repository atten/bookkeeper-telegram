package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "sber")
public class SberReplenishSmsByFpsParser extends RegexpSpendingParser<SberReplenishSms> {
    public SberReplenishSmsByFpsParser() {
        super(
            SberReplenishSms.class,
            ACCOUNT_FIELD,
            TIME,
            "Выплата кешбэка по СБП",
            AMOUNT_FIELD + CURRENCY_FIELD,
            OPTIONAL_TEXT,
            "Баланс:",
            AMOUNT_FIELD + CURRENCY_FIELD
        );
    }
}
