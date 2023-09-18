package bookkeeper.telegram.shared;

import bookkeeper.service.registry.CallbackMessageRegistry;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

import java.io.Serializable;
import java.time.LocalDate;

import static bookkeeper.telegram.shared.StringUtil.getMonthName;

public abstract class CallbackMessage implements Serializable {

    public InlineKeyboardButton asButton(String text) {
        return CallbackMessageRegistry.createButton(this, text);
    }
    private InlineKeyboardButton asMonthButton(LocalDate relativeDate, int offset, boolean forward) {
        var monthName = getMonthName(relativeDate.plusMonths(offset));
        if (forward)
            return asButton(String.format("В %s ▶️", monthName));
        return asButton(String.format("◀️ В %s", monthName));
    }

    public InlineKeyboardButton asNextMonthButton(int offset) {
        return asMonthButton(LocalDate.now(), offset, true);
    }

    public InlineKeyboardButton asNextMonthButton(LocalDate relativeDate, int offset) {
        return asMonthButton(relativeDate, offset, true);
    }

    public InlineKeyboardButton asPrevMonthButton(int offset) {
        return asMonthButton(LocalDate.now(), offset, false);
    }

    public InlineKeyboardButton asPrevMonthButton(LocalDate relativeDate, int offset) {
        return asMonthButton(relativeDate, offset, false);
    }
}
