package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.SpendingParser;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Currency;

import static bookkeeper.service.telegram.StringUtils.parseAmount;

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

        sms.purchaseSum = parseAmount(parts[4]);
        sms.purchaseCurrency = currency;
        sms.merchant = String.join(" ", Arrays.copyOfRange(parts, 6, parts.length - 3));
        sms.accountBalance = parseAmount(parts[parts.length - 2]);
        sms.accountCurrency = Currency.getInstance(parts[parts.length - 1]);

        // remove trailing dot
        sms.merchant = sms.merchant.substring(0, sms.merchant.length() - 1);

        return sms;
    }
}
