package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "sber")
public class SberReplenishSmsWithSenderParser extends RegexpSpendingParser<SberReplenishSmsWithSender> {
    public SberReplenishSmsWithSenderParser() {
        super(
            SberReplenishSmsWithSender.class,
            ACCOUNT_FIELD,
            TIME,
            "Перевод" + OPTIONAL_TEXT,
            AMOUNT_FIELD + CURRENCY_FIELD,
            "от",
            TEXT_FIELD,
            "Баланс:",
            AMOUNT_FIELD + CURRENCY_FIELD
        );
    }
}
