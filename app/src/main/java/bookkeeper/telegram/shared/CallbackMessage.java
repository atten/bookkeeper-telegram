package bookkeeper.telegram.shared;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public abstract class CallbackMessage implements Serializable {

    public InlineKeyboardButton asButton(String text) {
        return CallbackMessageRegistry.createButton(this, text);
    }
    private InlineKeyboardButton asMonthButton(LocalDate relativeDate, int offset, boolean forward) {
        var monthName = relativeDate.plusMonths(offset).getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());
        if (forward)
            return asButton(String.format("В %s ▶️", monthName));
        return asButton(String.format("◀️ В %s", monthName));
    }

    public InlineKeyboardButton asNextMonthButton(LocalDate relativeDate, int offset) {
        return asMonthButton(relativeDate, offset, true);
    }

    public InlineKeyboardButton asPrevMonthButton(LocalDate relativeDate, int offset) {
        return asMonthButton(relativeDate, offset, false);
    }
}
