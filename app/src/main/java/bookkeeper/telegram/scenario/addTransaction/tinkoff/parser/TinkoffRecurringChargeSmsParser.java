package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.SpendingParser;

import java.text.ParseException;
import java.util.Currency;

import static bookkeeper.service.telegram.StringUtils.parseAmount;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffRecurringChargeSmsParser implements SpendingParser<TinkoffRecurringChargeSms> {

    @Override
    public TinkoffRecurringChargeSms parse(String rawMessage) throws ParseException {
        String[] parts = rawMessage.split(" ");
        if (!rawMessage.startsWith("Выполнен регулярный платеж") || parts.length < 7)
            throw new ParseException(rawMessage, 0);

        var currencyPart = parts[parts.length - 1].replace(".", "").replace("р", "RUB");
        var chargeSumPart = rawMessage.substring(rawMessage.lastIndexOf('"') + 5, rawMessage.lastIndexOf(' ')).replace(" ", "");
        var sms = new TinkoffRecurringChargeSms();
        Currency currency;

        try {
            currency = Currency.getInstance(currencyPart);
        } catch (IllegalArgumentException e) {
            throw new ParseException(String.format("Cannot parse currency: %s", e), 0);
        }

        sms.chargeSum = parseAmount(chargeSumPart);
        sms.chargeCurrency = currency;
        sms.destination = rawMessage.substring(rawMessage.indexOf('"') + 1, rawMessage.lastIndexOf('"'));
        return sms;
    }
}
