package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "sber")
public class SberRecurringChargeSmsParser extends RegexpSpendingParser<SberRecurringChargeSms> {
    public SberRecurringChargeSmsParser() {
        super(
            SberRecurringChargeSms.class,
            ACCOUNT_FIELD,
            TIME,
            "Оплата",
            AMOUNT_FIELD + CURRENCY_FIELD,
            TEXT_FIELD,
            "Следующее списание",
            DATE,
            "Баланс",
            AMOUNT_FIELD + CURRENCY_FIELD
        );
    }

    @Override
    public int weight() {
        return 1;
    }
}
