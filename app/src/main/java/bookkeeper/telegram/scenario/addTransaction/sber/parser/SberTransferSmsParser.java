package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "sber")
public class SberTransferSmsParser extends RegexpSpendingParser<SberTransferSms> {
    public SberTransferSmsParser() {
        super(
            SberTransferSms.class,
            ACCOUNT_FIELD,
            TIME,
            nonCapturingGroup("Перевод", "оплата"),
            AMOUNT_FIELD + CURRENCY_FIELD,
            "Баланс:",
            AMOUNT_FIELD + CURRENCY_FIELD
        );
    }
}
