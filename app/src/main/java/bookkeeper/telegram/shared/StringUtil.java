package bookkeeper.telegram.shared;

import bookkeeper.entity.Account;
import bookkeeper.entity.AccountTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;

public class StringUtil {
    public static String ICON_ACCOUNT = "\uD83D\uDCD8";       // 📘
    public static String ICON_RATES = "\uD83D\uDCC8";         // 📈
    public static String ICON_ASSETS = "\uD83C\uDFDB";        // 🏛
    static String ICON_EXPENDITURE = "\uD83D\uDCDD";          // 📝
    static String ICON_DELETE = "\uD83D\uDDD1";               // 🗑

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

    static String getAmount(BigDecimal amount, Account account) {
        return getAmount(amount, account.getCurrency());
    }

    /**
     * 100 ₽ (credit) | +100 ₽ (debit)
     */
    public static String getAmount(BigDecimal amount, Currency currency) {
        return String.format(
            "%s %s",
            amount.negate().toString().replace("-", "+"),
            currency.getSymbol()
        );
    }

    /**
     * Январь
     */
    public static String getMonthName(int monthOffset) {
        var date = LocalDate.now().plusMonths(monthOffset);
        return getMonthName(date.plusMonths(monthOffset));
    }

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
     * Replace non-breaking spaces with regular one.
     */
    static String cleanString(String input) {
        return input.replaceAll(Arrays.toString(Character.toChars(160)), " ");
    }

    public static String strikeoutMessage(String message) {
        return String.format("<del>%s</del>", message);
    }
}
