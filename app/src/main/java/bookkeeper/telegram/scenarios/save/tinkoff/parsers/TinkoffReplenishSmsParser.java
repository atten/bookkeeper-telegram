package bookkeeper.telegram.scenarios.save.tinkoff.parsers;

import bookkeeper.services.parsers.MarkSpendingParser;
import bookkeeper.services.parsers.SpendingParser;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffReplenishSmsParser implements SpendingParser<TinkoffReplenishSms> {

    @Override
    public TinkoffReplenishSms parse(String rawMessage) throws ParseException {
        rawMessage = rawMessage.replace("Возврат. Счет", "Пополнение, счет");

        String[] parts = rawMessage.split(" ");
        if (!rawMessage.startsWith("Пополнение, счет") || parts.length != 8)
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
