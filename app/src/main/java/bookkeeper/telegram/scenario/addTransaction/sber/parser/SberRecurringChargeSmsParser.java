package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.SpendingParser;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;
import java.util.regex.Pattern;

@MarkSpendingParser(provider = "sber")
public class SberRecurringChargeSmsParser implements SpendingParser<SberRecurringChargeSms> {
    private final Pattern pattern = Pattern.compile(
        "(\\S+)\\s([\\d:]+)\\sОплата\\s([\\d\\s.]+)(\\D)\\s(.+).\\sСледующее списание\\s([\\d.]+)\\sБаланс\\s([\\d\\s,]+)(\\D)",
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    @Override
    public SberRecurringChargeSms parse(String rawMessage) throws ParseException {
        var matcher = pattern.matcher(rawMessage);
        if (!matcher.find()) {
            throw new ParseException(rawMessage, 0);
        }

        var accountName = matcher.group(1);
        var chargeSum = matcher.group(3);
        var chargeCurrency = matcher.group(4);
        var destination = matcher.group(5);
        var accountBalance = matcher.group(7);
        var accountCurrency = matcher.group(8);

        chargeSum = chargeSum.replace(" ", "");
        chargeCurrency = chargeCurrency.replace("р", "RUB");
        accountBalance = accountBalance.replace(" ", "").replace(",", ".");
        accountCurrency = accountCurrency.replace("р", "RUB");

        var sms = new SberRecurringChargeSms();
        sms.setAccountName(accountName);
        sms.setChargeSum(new BigDecimal(chargeSum));
        sms.setChargeCurrency(Currency.getInstance(chargeCurrency));
        sms.setDestination(destination);
        sms.setAccountBalance(new BigDecimal(accountBalance));
        sms.setAccountCurrency(Currency.getInstance(accountCurrency));
        return sms;
    }
}
