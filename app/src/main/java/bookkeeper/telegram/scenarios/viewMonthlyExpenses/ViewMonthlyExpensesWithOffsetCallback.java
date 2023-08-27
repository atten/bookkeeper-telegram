package bookkeeper.telegram.scenarios.viewMonthlyExpenses;

import bookkeeper.telegram.shared.CallbackMessage;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


class ViewMonthlyExpensesWithOffsetCallback extends CallbackMessage {
    private final int monthOffset;

    ViewMonthlyExpensesWithOffsetCallback(int monthOffset) {
        this.monthOffset = monthOffset;
    }

    int getMonthOffset() {
        return monthOffset;
    }

    InlineKeyboardButton asButton(boolean backward) {
        var text = StringUtils.capitalize(LocalDate.now().plusMonths(monthOffset).format(DateTimeFormatter.ofPattern("MMM yy")));
        if (backward)
            return asButton(String.format("◀️ %s", text));
        return asButton(String.format("%s ▶️", text));
    }
}
