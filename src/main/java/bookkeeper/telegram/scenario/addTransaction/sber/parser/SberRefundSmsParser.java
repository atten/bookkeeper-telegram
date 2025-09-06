package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "sber")
public class SberRefundSmsParser extends RegexpSpendingParser<SberRefundSms> {
    public SberRefundSmsParser() {
        super(
            SberRefundSms.class,
            ACCOUNT_FIELD,
            DATETIME,
            nonCapturingGroup("Отмена", "возврат") + OPTIONAL_TEXT,
            AMOUNT_FIELD + CURRENCY_FIELD,
            TEXT_FIELD,
            "Баланс:",
            AMOUNT_FIELD + CURRENCY_FIELD
        );
    }
}
