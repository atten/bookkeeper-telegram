package bookkeeper.telegram.scenarios.save.tinkoff.parsers;

import bookkeeper.services.parsers.SpendingParser;
import bookkeeper.services.parsers.MarkSpendingParser;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffPurchaseSmsWithDateParser implements SpendingParser<TinkoffPurchaseSmsWithDate> {

    @Override
    public TinkoffPurchaseSmsWithDate parse(String rawMessage) throws ParseException {
        var parts = rawMessage.split(" ");
        if (parts.length < 2)
            throw new ParseException(rawMessage, 0);

        var datePart = parts[1].substring(0, parts[1].length() - 1);
        var smsWithoutDateText = rawMessage.replace(" " + datePart + ". К", ", к");

        // parse date and reuse TinkoffPurchaseSmsParser for the rest of fields
        var smsWithoutDate = new TinkoffPurchaseSmsParser().parse(smsWithoutDateText);
        var sms = new TinkoffPurchaseSmsWithDate();

        try {
            sms.purchaseDate = LocalDate.parse(datePart, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (DateTimeParseException e) {
            throw new ParseException(rawMessage, 0);
        }
        sms.cardIdentifier = smsWithoutDate.cardIdentifier;
        sms.purchaseSum = smsWithoutDate.purchaseSum;
        sms.purchaseCurrency = smsWithoutDate.purchaseCurrency;
        sms.merchant = smsWithoutDate.merchant;
        sms.accountBalance = smsWithoutDate.accountBalance;
        sms.accountCurrency = smsWithoutDate.accountCurrency;

        return sms;
    }
}
