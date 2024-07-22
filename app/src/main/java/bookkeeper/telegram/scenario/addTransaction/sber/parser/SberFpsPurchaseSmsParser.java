package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.MarkSpendingParser;
import bookkeeper.service.parser.SpendingParser;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;
import java.util.regex.Pattern;

@MarkSpendingParser(provider = "sber")
public class SberFpsPurchaseSmsParser implements SpendingParser<SberFpsPurchaseSms> {
    private final Pattern pattern = Pattern.compile(
        "(\\S+)\\s([\\d:]+)\\sПокупка по СБП\\s([\\d\\s.]+)(\\D)\\s(.+)\\sБаланс:\\s([\\d\\s.]+)(\\D)",
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    @Override
    public SberFpsPurchaseSms parse(String rawMessage) throws ParseException {
        var matcher = pattern.matcher(rawMessage);
        if (!matcher.find()) {
            throw new ParseException(rawMessage, 0);
        }

        var accountName = matcher.group(1);
        var purchaseSum = matcher.group(3);
        var purchaseCurrency = matcher.group(4);
        var merchant = matcher.group(5);
        var accountBalance = matcher.group(6);
        var accountCurrency = matcher.group(7);

        purchaseSum = purchaseSum.replace(" ", "");
        purchaseCurrency = purchaseCurrency.replace("р", "RUB");
        accountBalance = accountBalance.replace(" ", "");
        accountCurrency = accountCurrency.replace("р", "RUB");

        var sms = new SberFpsPurchaseSms();
        sms.setAccountName(accountName);
        sms.setPurchaseSum(new BigDecimal(purchaseSum));
        sms.setPurchaseCurrency(Currency.getInstance(purchaseCurrency));
        sms.setMerchant(merchant);
        sms.setAccountBalance(new BigDecimal(accountBalance));
        sms.setAccountCurrency(Currency.getInstance(accountCurrency));
        return sms;
    }
}
