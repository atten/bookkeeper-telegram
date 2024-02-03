package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.SpendingParser;
import bookkeeper.service.parser.MarkSpendingParser;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Currency;

import static bookkeeper.service.telegram.StringUtils.parseAmount;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffTransferSmsParser implements SpendingParser<TinkoffTransferSms> {

    @Override
    public TinkoffTransferSms parse(String rawMessage) throws ParseException {
        rawMessage = rawMessage.replace("Платеж. Счет", "Перевод. Счет");

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

        sms.transferSum = parseAmount(parts[3]);
        sms.transferCurrency = currency;
        sms.destination = String.join(" ", Arrays.copyOfRange(parts, 5, parts.length - 3));
        sms.accountBalance = parseAmount(parts[parts.length - 2]);
        sms.accountCurrency = Currency.getInstance(parts[parts.length - 1]);

        // remove trailing dot
        sms.destination = sms.destination.substring(0, sms.destination.length() - 1);

        return sms;
    }
}
