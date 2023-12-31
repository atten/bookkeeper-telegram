package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.SpendingParser;
import bookkeeper.service.parser.MarkSpendingParser;

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

        var datePatterns = new String[]{"dd.MM.yyyy", "d.MM.yyyy"};
        for (var datePattern : datePatterns) {
            try {
                sms.purchaseDate = LocalDate.parse(datePart, DateTimeFormatter.ofPattern(datePattern));
            } catch (DateTimeParseException ignored) {}
        }

        if (sms.purchaseDate == null) {
            // no patterns matched
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
