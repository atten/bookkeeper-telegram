package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.SpendingParser;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Currency;

import static bookkeeper.service.telegram.StringUtils.parseAmount;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffWithdrawSmsParser implements SpendingParser<TinkoffWithdrawalSms> {

    @Override
    public TinkoffWithdrawalSms parse(String rawMessage) throws ParseException {
        String[] parts = rawMessage.split(" ");
        if (!rawMessage.startsWith("Снятие, карта") || parts.length < 9)
            throw new ParseException(rawMessage, 0);

        var sms = new TinkoffWithdrawalSms();
        Currency currency;

        try {
            currency = Currency.getInstance(parts[4].replace(".", ""));
        } catch (IllegalArgumentException e) {
            throw new ParseException("Cannot parse currency: %s".formatted(e), 0);
        }

        sms.cardIdentifier = parts[2].replace(".", "");
        sms.withdrawSum = parseAmount(parts[3]);
        sms.withdrawCurrency = currency;
        sms.casher = String.join(" ", Arrays.copyOfRange(parts, 5, parts.length - 3));
        sms.accountBalance = parseAmount(parts[parts.length - 2]);
        sms.accountCurrency = Currency.getInstance(parts[parts.length - 1]);

        // remove trailing dot
        sms.casher = sms.casher.substring(0, sms.casher.length() - 1);

        return sms;
    }
}
