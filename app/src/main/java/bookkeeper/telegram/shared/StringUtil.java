package bookkeeper.telegram.shared;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Locale;

public class StringUtil {
    /**
     * Январь
     */
    public static String getMonthName(int monthOffset) {
        var date = LocalDate.now().plusMonths(monthOffset);
        return getMonthName(date.plusMonths(monthOffset));
    }

    public static String getMonthName(LocalDate date) {
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
}
