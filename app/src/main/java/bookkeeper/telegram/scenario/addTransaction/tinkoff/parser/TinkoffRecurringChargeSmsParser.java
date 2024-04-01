package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.SpendingParser;

import java.text.ParseException;
import java.util.Currency;
import java.util.List;

import static bookkeeper.service.telegram.StringUtils.parseAmount;

@MarkSpendingParser(provider = "tinkoff")
public class TinkoffRecurringChargeSmsParser implements SpendingParser<TinkoffRecurringChargeSms> {
    private final List<String> variants = List.of(
        "Выполнен регулярный платеж",
        "Выполнен автоплатеж"
    );

    @Override
    public TinkoffRecurringChargeSms parse(String rawMessage) throws ParseException {
        variants.stream().filter(rawMessage::contains).findAny().orElseThrow(() -> new ParseException(rawMessage, 0));

        var cleanRawMessage = rawMessage
            .replace("«", "\"")
            .replace("»", "\"");
        String[] parts = cleanRawMessage.split(" ");
        var currencyPart = parts[parts.length - 1].replace(".", "").replace("р", "RUB");
        var chargeSumPart = cleanRawMessage.substring(cleanRawMessage.lastIndexOf(" на ") + 4, cleanRawMessage.lastIndexOf(' ')).replace(" ", "");
        var sms = new TinkoffRecurringChargeSms();
        Currency currency;

        try {
            currency = Currency.getInstance(currencyPart);
        } catch (IllegalArgumentException e) {
            throw new ParseException(String.format("Cannot parse currency: %s", e), 0);
        }

        sms.chargeSum = parseAmount(chargeSumPart);
        sms.chargeCurrency = currency;
        sms.destination = cleanRawMessage.substring(cleanRawMessage.indexOf('"') + 1, cleanRawMessage.lastIndexOf('"'));
        return sms;
    }
}
