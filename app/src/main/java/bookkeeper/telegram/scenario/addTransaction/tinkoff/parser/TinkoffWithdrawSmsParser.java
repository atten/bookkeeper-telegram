package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.RegexpSpendingParser;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffWithdrawSmsParser extends RegexpSpendingParser<TinkoffWithdrawalSms> {

    public TinkoffWithdrawSmsParser() {
        super(
            TinkoffWithdrawalSms.class,
            "Снятие, карта",
            TEXT_FIELD,
            AMOUNT_FIELD,
            CURRENCY_FIELD,
            TEXT_FIELD,
            "Доступно",
            AMOUNT_FIELD,
            CURRENCY_FIELD
        );
    }
}
