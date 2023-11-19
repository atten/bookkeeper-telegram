package bookkeeper.service.telegram;

import bookkeeper.entity.Account;
import bookkeeper.entity.AccountTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class StringUtils {
    public static String ICON_ACCOUNT = "\uD83D\uDCD8";       // 📘
    public static String ICON_RATES = "\uD83D\uDCC8";         // 📈
    public static String ICON_ASSETS = "\uD83C\uDFDB";        // 🏛
    public static String ICON_EXPENDITURE = "\uD83D\uDCDD";   // 📝
    public static String ICON_DELETE = "\uD83D\uDDD1";        // 🗑

    public static String getNumberIcon(int number) {
        switch (number) {
            case 1 -> { return "1️⃣"; }
            case 2 -> { return "2️⃣"; }
            case 3 -> { return "3️⃣"; }
            case 4 -> { return "4️⃣"; }
            case 5 -> { return "5️⃣"; }
            case 6 -> { return "6️⃣"; }
            case 7 -> { return "7️⃣"; }
            case 8 -> { return "8️⃣"; }
            case 9 -> { return "9️⃣"; }
            case 10 -> { return "\uD83D\uDD1F"; }
            default -> { return "\uD83D\uDD23"; }
        }
    }

    public static String getAmount(AccountTransaction transaction) {
        return getAmount(transaction.getAmount(), transaction.currency());
    }

    public static String getAmount(BigDecimal amount, Account account) {
        return getAmount(amount, account.getCurrency());
    }

    /**
     * 100.55 ₽ (credit) | +100.55 ₽ (debit)
     */
    public static String getAmount(BigDecimal amount, Currency currency) {
        return String.format(
            "%s %s",
            amount.negate().toString().replace("-", "+"),
            currency.getSymbol()
        );
    }

    /**
     * -10 101 ₽ (credit) | +10 101 ₽ (debit)
     */
    private static String getRoundedAmountSigned(BigDecimal amount, Currency currency) {
        return String.format(
            "%+,.0f %s",
            amount,
            currency.getSymbol()
        );
    }

    /**
     * 25 542 ₽, 10 $
     */
    public static String getRoundedAmountMulti(Map<Currency, BigDecimal> values) {
        if (values.isEmpty()) {
            return "0";
        }

        return values
            .entrySet()
            .stream()
            .filter(entry -> !entry.getValue().stripTrailingZeros().equals(BigDecimal.ZERO))
            .sorted(Comparator.comparing(entry -> entry.getValue().abs().negate()))
            .map(i -> getRoundedAmountSigned(i.getValue(), i.getKey()))
            .collect(Collectors.joining(", "));
    }

    /**
     * 📘Копилка (RUB)
     */
    public static String getAccountDisplayName(Account account) {
        var currency = account.getCurrency().getCurrencyCode();
        var name = account.getName();

        if (!name.contains(currency))
            name = String.format("%s (%s)", name, currency);

        if (Character.isLetterOrDigit(name.charAt(0)))
            name = ICON_ACCOUNT + name;

        return name;
    }

    /**
     * январь
     */
    public static String getMonthName(int monthOffset) {
        return getMonthName(LocalDate.now().plusMonths(monthOffset));
    }

    /**
     * январь
     */
    static String getMonthName(LocalDate date) {
        return date.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());
    }

    /**
     * Января 2020
     */
    public static String getMonthYearRelative(int monthOffset) {
        var date = LocalDate.now().plusMonths(monthOffset);
        return date.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
    }

    /**
     * янв. 2020
     */
    public static String getMonthYearShort(int monthOffset) {
        var date = LocalDate.now().plusMonths(monthOffset);
        return date.format(DateTimeFormatter.ofPattern("MMM yyyy"));
    }

    /**
     * dd.MM.yyyy
     */
    public static String getDateShort(LocalDate date) {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
    }

    private static String pluralize(Integer count, String single, String few, String many) {
        var n = count % 100;
        if (count == 0)
            return many;
        if (n >= 5 && n <= 20)
            return many;
        if (count % 10 == 1)
            return single;
        if (count % 10 <= 4)
            return few;
        return many;
    }

    /**
     * pluralize for %s-strings
     */
    public static String pluralizeTemplate(Integer count, String single, String few, String many) {
        return String.format(pluralize(count, single, few, many), count);
    }

    /**
     * Prepare raw input string for parsers:
     * - replace non-breaking spaces, double spaces with regular one
     * - strip spaces
     */
    public static String cleanString(String input) {
        return input
            .replaceAll(Arrays.toString(Character.toChars(160)), " ")
            .replace("  ", " ")
            .strip();
    }

    public static String strikeoutMessage(String message) {
        return String.format("<del>%s</del>", message);
    }
}
