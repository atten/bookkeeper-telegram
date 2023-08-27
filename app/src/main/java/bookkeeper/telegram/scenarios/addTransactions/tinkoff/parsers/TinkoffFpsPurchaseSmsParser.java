package bookkeeper.telegram.scenarios.addTransactions.tinkoff.parsers;

import bookkeeper.services.parsers.SpendingParser;
import bookkeeper.services.parsers.MarkSpendingParser;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Currency;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffFpsPurchaseSmsParser implements SpendingParser<TinkoffFpsPurchaseSms> {

    @Override
    public TinkoffFpsPurchaseSms parse(String rawMessage) throws ParseException {
        String[] parts = rawMessage.split(" ");
        if (!rawMessage.startsWith("Оплата СБП, счет") || parts.length < 10)
            throw new ParseException(rawMessage, 0);

        var sms = new TinkoffFpsPurchaseSms();
        Currency currency;

        try {
            currency = Currency.getInstance(parts[5].replace(".", ""));
        } catch (IllegalArgumentException e) {
            throw new ParseException(String.format("Cannot parse currency: %s", e), 0);
        }

        sms.purchaseSum = new BigDecimal(parts[4]);
        sms.purchaseCurrency = currency;
        sms.merchant = String.join(" ", Arrays.copyOfRange(parts, 6, parts.length - 3));
        sms.accountBalance = new BigDecimal(parts[parts.length - 2]);
        sms.accountCurrency = Currency.getInstance(parts[parts.length - 1]);

        // remove trailing dot
        sms.merchant = sms.merchant.substring(0, sms.merchant.length() - 1);

        return sms;
    }
}
