package bookkeeper.service.parser;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;

public class RegexpSpendingParser<T extends Spending> implements SpendingParser<T> {
    protected static String ACCOUNT_FIELD = "(\\S+)";
    protected static String DATE = "[\\d.]+?";
    protected static String TIME = "[\\d:]+";
    protected static String AMOUNT_FIELD = "([\\d\\s.,]+)";
    protected static String CURRENCY_FIELD = "(\\D+?)";
    protected static String OPTIONAL_TEXT = ".*?";
    protected static String TEXT_FIELD = "(.+?)";

    private final Pattern pattern;
    private final Class<T> spendingClass;

    protected static String nonCapturingGroup(String... fieldPatterns) {
        var builder = new StringJoiner("|");
        for (var pattern : fieldPatterns) {
            builder.add(pattern);
        }
        return "(?:" + builder + ")";
    }

    public RegexpSpendingParser(Class<T> clazz, String... fieldPatterns) {
        String delimiter = "[.\\s]+";
        String terminator = "$";

        var builder = new StringJoiner(delimiter);
        for (var pattern : fieldPatterns) {
            builder.add(pattern);
        }

        this.pattern = Pattern.compile(builder + terminator, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        this.spendingClass = clazz;
    }

    @Override
    public T parse(String rawMessage) throws ParseException {
        var groups = parseGroups(rawMessage);
        T spending;
        try {
            spending = spendingClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        var index = 0;
        for (var field: spendingClass.getDeclaredFields()) {
            String rawValue = groups.get(index);
            Object value;
            if (field.getType().equals(Currency.class)) {
                value = parseCurrency(rawValue);
            } else if (field.getType().equals(BigDecimal.class)) {
                value = parseAmount(rawValue);
            } else {
                value = rawValue;
            }
            try {
                field.set(spending, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            index++;
        }

        return spending;
    }

    private List<String> parseGroups(String input) throws ParseException {
        var matcher = pattern.matcher(input);
        if (!matcher.find()) {
            throw new ParseException(input, 0);
        }

        String[] groups = new String[matcher.groupCount()];

        for (int i = 0; i < groups.length; i++) {
            groups[i] = matcher.group(i + 1);
        }

        return List.of(groups);
    }

    private static BigDecimal parseAmount(String amount) {
        return new BigDecimal(
            amount
                .replace(" ", "")
                .replace(",", ".")
        );
    }

    private static Currency parseCurrency(String currency) throws ParseException {
        var value = currency.replace("р", "RUB");
        try {
            return Currency.getInstance(value);
        } catch (IllegalArgumentException e) {
            throw new ParseException(currency, 0);
        }
    }
}
