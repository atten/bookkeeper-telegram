package bookkeeper.telegram.shared;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Locale;

public class StringUtil {
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
}
