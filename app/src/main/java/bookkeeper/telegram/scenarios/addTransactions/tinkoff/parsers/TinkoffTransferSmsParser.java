package bookkeeper.telegram.scenarios.addTransactions.tinkoff.parsers;

import bookkeeper.services.parsers.SpendingParser;
import bookkeeper.services.parsers.MarkSpendingParser;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Currency;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffTransferSmsParser implements SpendingParser<TinkoffTransferSms> {

    @Override
    public TinkoffTransferSms parse(String rawMessage) throws ParseException {
        String[] parts = rawMessage.split(" ");
        if (!rawMessage.startsWith("Перевод. Счет") || parts.length < 9)
            throw new ParseException(rawMessage, 0);

        var sms = new TinkoffTransferSms();
        Currency currency;

        try {
            currency = Currency.getInstance(parts[4].replace(".", ""));
        } catch (IllegalArgumentException e) {
            throw new ParseException(String.format("Cannot parse currency: %s", e), 0);
        }

        sms.transferSum = new BigDecimal(parts[3]);
        sms.transferCurrency = currency;
        sms.destination = String.join(" ", Arrays.copyOfRange(parts, 5, parts.length - 3));
        sms.accountBalance = new BigDecimal(parts[parts.length - 2]);
        sms.accountCurrency = Currency.getInstance(parts[parts.length - 1]);

        // remove trailing dot
        sms.destination = sms.destination.substring(0, sms.destination.length() - 1);

        return sms;
    }
}
