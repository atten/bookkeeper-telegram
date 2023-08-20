package bookkeeper.telegram.scenarios.store.tinkoff.parsers;

import bookkeeper.services.parsers.MarkSpendingParser;
import bookkeeper.services.parsers.SpendingParser;

import java.text.ParseException;
import java.util.Arrays;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffReplenishWithSenderSmsParser implements SpendingParser<TinkoffReplenishSms> {

    @Override
    public TinkoffReplenishWithSenderSms parse(String rawMessage) throws ParseException {
        String[] parts = rawMessage.split(" ");
        if (!rawMessage.startsWith("Пополнение, счет") || parts.length < 9)
            throw new ParseException(rawMessage, 0);

        var senderPart = String.join(" ", Arrays.copyOfRange(parts, 5, parts.length - 3));
        var smsWithoutSenderText = rawMessage.replace(senderPart + " ", "");

        // parse sender and reuse TinkoffReplenishSmsParser for the rest of fields
        var smsWithoutSender = new TinkoffReplenishSmsParser().parse(smsWithoutSenderText);
        var sms = new TinkoffReplenishWithSenderSms();

        sms.replenishSender = senderPart.replace(".", "");
        sms.replenishSum = smsWithoutSender.replenishSum;
        sms.replenishCurrency = smsWithoutSender.replenishCurrency;
        sms.accountBalance = smsWithoutSender.accountBalance;
        sms.accountCurrency = smsWithoutSender.accountCurrency;

        return sms;
    }
}
