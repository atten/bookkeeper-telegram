package bookkeeper.services.parsers;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Currency;

public class TinkoffPurchaseSmsParser implements BankingMessageParser<TinkoffPurchaseSms> {

    @Override
    public TinkoffPurchaseSms parse(String rawMessage) throws ParseException {
        String[] parts = rawMessage.split(" ");
        if (!rawMessage.startsWith("Покупка, карта") || parts.length < 9)
            throw new ParseException(rawMessage, 0);

        TinkoffPurchaseSms sms = new TinkoffPurchaseSms();

        sms.cardIdentifier = parts[2].replace(".", "");
        sms.purchaseSum = new BigDecimal(parts[3]);
        sms.purchaseCurrency = Currency.getInstance(parts[4].replace(".", ""));
        sms.merchantTag = String.join(" ", Arrays.copyOfRange(parts, 5, parts.length - 3));
        sms.accountBalance = new BigDecimal(parts[parts.length - 2]);
        sms.accountCurrency = Currency.getInstance(parts[parts.length - 1]);

        // remove trailing dot
        sms.merchantTag = sms.merchantTag.substring(0, sms.merchantTag.length() - 1);

        return sms;
    }
}
