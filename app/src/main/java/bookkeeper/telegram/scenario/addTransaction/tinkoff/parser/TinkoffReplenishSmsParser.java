package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.SpendingParser;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffReplenishSmsParser implements SpendingParser<TinkoffReplenishSms> {

    @Override
    public TinkoffReplenishSms parse(String rawMessage) throws ParseException {
        var start = "Пополнение, счет";

        rawMessage = rawMessage.replace("Пополнение. Счет", start);
        rawMessage = rawMessage.replace("Возврат. Счет", start);
        rawMessage = rawMessage.replace("Возврат СБП, счет", start);

        String[] parts = rawMessage.split(" ");
        if (!rawMessage.startsWith(start) || parts.length != 8)
            throw new ParseException(rawMessage, 0);

        var sms = new TinkoffReplenishSms();
        Currency replenishCurrency;

        try {
            replenishCurrency = Currency.getInstance(parts[4].replace(".", ""));
        } catch (IllegalArgumentException e) {
            throw new ParseException(String.format("Cannot parse currency: %s", e), 0);
        }

        sms.replenishCurrency = replenishCurrency;
        sms.replenishSum = new BigDecimal(parts[3]);
        sms.accountBalance = new BigDecimal(parts[parts.length - 2]);
        sms.accountCurrency = Currency.getInstance(parts[parts.length - 1]);
        return sms;
    }
}
