package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.SpendingParser;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Currency;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffDepositInterestSmsParser implements SpendingParser<TinkoffDepositInterestSms> {

    @Override
    public TinkoffDepositInterestSms parse(String rawMessage) throws ParseException {
        String[] parts = rawMessage.split(" ");
        if (!rawMessage.startsWith("Выплата процентов по вкладу") || parts.length < 6)
            throw new ParseException(rawMessage, 0);

        var sms = new TinkoffDepositInterestSms();
        try {
            sms.setInterestCurrency(Currency.getInstance(parts[parts.length - 1]));
        } catch (IllegalArgumentException e) {
            throw new ParseException(String.format("Cannot parse currency: %s", e), 0);
        }

        sms.setInterestSum(new BigDecimal(String.join("", Arrays.copyOfRange(parts, 4, parts.length - 1))));
        return sms;
    }
}
