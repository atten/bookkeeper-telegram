package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.SpendingParser;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;
import java.util.regex.Pattern;

@MarkSpendingParser(provider = "sber")
public class SberReplenishSmsParser implements SpendingParser<SberReplenishSms> {
    private final Pattern pattern = Pattern.compile(
        "(\\S+)\\s([\\d:]+)\\sЗачисление\\s([\\d\\s.]+)(\\D)\\sБаланс:\\s([\\d\\s.]+)(\\D)",
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    @Override
    public SberReplenishSms parse(String rawMessage) throws ParseException {
        var matcher = pattern.matcher(rawMessage);
        if (!matcher.find()) {
            throw new ParseException(rawMessage, 0);
        }

        var accountName = matcher.group(1);
        var replenishSum = matcher.group(3);
        var replenishCurrency = matcher.group(4);
        var accountBalance = matcher.group(5);
        var accountCurrency = matcher.group(6);

        replenishSum = replenishSum.replace(" ", "");
        replenishCurrency = replenishCurrency.replace("р", "RUB");
        accountBalance = accountBalance.replace(" ", "");
        accountCurrency = accountCurrency.replace("р", "RUB");

        var sms = new SberReplenishSms();
        sms.setAccountName(accountName);
        sms.setReplenishSum(new BigDecimal(replenishSum));
        sms.setReplenishCurrency(Currency.getInstance(replenishCurrency));
        sms.setAccountBalance(new BigDecimal(accountBalance));
        sms.setAccountCurrency(Currency.getInstance(accountCurrency));
        return sms;
    }
}
