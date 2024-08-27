package bookkeeper.service.telegram;

import bookkeeper.dao.entity.Account;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDate;

import static bookkeeper.service.telegram.StringUtils.getAccountDisplayName;
import static bookkeeper.service.telegram.StringUtils.getMonthName;

public abstract class CallbackMessage implements Serializable {

    public InlineKeyboardButton asButton(String text) {
        return CallbackMessageRegistry.createButton(this, text);
    }

    public InlineKeyboardButton asAccountButton(Account account) {
        return asButton(getAccountDisplayName(account));
    }

    public InlineKeyboardButton asNextMonthButton(int offset) {
        return asMonthButton(LocalDate.now(), offset, true, "%s");
    }

    public InlineKeyboardButton asPrevMonthButton(int offset) {
        return asMonthButton(LocalDate.now(), offset, false, "%s");
    }

    public InlineKeyboardButton asNextMonthButton(LocalDate relativeDate, String template) {
        return asMonthButton(relativeDate, 1, true, template);
    }

    public InlineKeyboardButton asPrevMonthButton(LocalDate relativeDate, String template) {
        return asMonthButton(relativeDate, -1, false, template);
    }

    private InlineKeyboardButton asMonthButton(LocalDate relativeDate, int offset, boolean forward, String template) {
        var monthName = getMonthName(relativeDate.plusMonths(offset));

        if (template.startsWith("%s"))
            monthName = StringUtils.capitalize(monthName);

        var templated = String.format(template, monthName);

        if (forward)
            return asButton(String.format("%s ▶️", templated));
        return asButton(String.format("◀️ %s", templated));
    }
}
