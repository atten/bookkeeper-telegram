package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "sber")
public class SberDepositInterestSmsParser extends RegexpSpendingParser<SberDepositInterestSms> {
    public SberDepositInterestSmsParser() {
        super(
            SberDepositInterestSms.class,
            TEXT_FIELD,
            ACCOUNT_FIELD,
            "Капитализация на",
            AMOUNT_FIELD + CURRENCY_FIELD,
            "Баланс:",
            AMOUNT_FIELD + CURRENCY_FIELD,
            TEXT_FIELD
        );
    }
}
