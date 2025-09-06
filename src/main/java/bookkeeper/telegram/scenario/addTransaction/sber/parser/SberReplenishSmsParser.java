package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "sber")
public class SberReplenishSmsParser extends RegexpSpendingParser<SberReplenishSms> {
    public SberReplenishSmsParser() {
        super(
            SberReplenishSms.class,
            ACCOUNT_FIELD,
            TIME,
            "Зачисление" + OPTIONAL_TEXT,
            AMOUNT_FIELD + CURRENCY_FIELD,
            "Баланс:",
            AMOUNT_FIELD + CURRENCY_FIELD
        );
    }
}
