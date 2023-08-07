package bookkeeper.services.parsers;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TinkoffPurchaseSmsWithDateParser implements BankingMessageParser<TinkoffPurchaseSmsWithDate> {

    @Override
    public TinkoffPurchaseSmsWithDate parse(String rawMessage) throws ParseException {
        String[] parts = rawMessage.split(" ");
        if (parts.length < 2)
            throw new ParseException(rawMessage, 0);

        String datePart = parts[1].substring(0, parts[1].length() - 1);
        String smsWithoutDateText = rawMessage.replace(" " + datePart + ". К", ", к");

        // parse date and reuse TinkoffPurchaseSmsParser for the rest of fields
        TinkoffPurchaseSms smsWithoutDate = new TinkoffPurchaseSmsParser().parse(smsWithoutDateText);
        TinkoffPurchaseSmsWithDate sms = new TinkoffPurchaseSmsWithDate();

        try {
            sms.purchaseDate = LocalDate.parse(datePart, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (DateTimeParseException e) {
            throw new ParseException(rawMessage, 0);
        }
        sms.cardIdentifier = smsWithoutDate.cardIdentifier;
        sms.purchaseSum = smsWithoutDate.purchaseSum;
        sms.purchaseCurrency = smsWithoutDate.purchaseCurrency;
        sms.merchantTag = smsWithoutDate.merchantTag;
        sms.accountBalance = smsWithoutDate.accountBalance;
        sms.accountCurrency = smsWithoutDate.accountCurrency;

        return sms;
    }
}
