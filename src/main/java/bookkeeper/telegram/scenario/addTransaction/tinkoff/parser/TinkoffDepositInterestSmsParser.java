package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffDepositInterestSmsParser extends RegexpSpendingParser<TinkoffDepositInterestSms> {
    public TinkoffDepositInterestSmsParser() {
        super(
            TinkoffDepositInterestSms.class,
            nonCapturingGroup("Выплата процентов по вкладу", "Зачислили проценты на вклад"),
            AMOUNT_FIELD,
            CURRENCY_FIELD_GREEDY + OPTIONAL_TEXT
        );
    }
}
