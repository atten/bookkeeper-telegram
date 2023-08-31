package bookkeeper.telegram.scenarios.editTransactions;

import bookkeeper.telegram.shared.CallbackMessage;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class ShiftTransactionMonthCallback extends CallbackMessage {
    private final long transactionId;
    private final long monthOffset;
    private List<Long> pendingTransactionIds = List.of();

    public ShiftTransactionMonthCallback(long transactionId, int monthOffset) {
        this.transactionId = transactionId;
        this.monthOffset = monthOffset;
    }

    long getTransactionId() {
        return transactionId;
    }

    long getMonthOffset() {
        return monthOffset;
    }

    List<Long> getPendingTransactionIds() {
        return pendingTransactionIds;
    }

    public ShiftTransactionMonthCallback setPendingTransactionIds(List<Long> pendingTransactionIds) {
        this.pendingTransactionIds = pendingTransactionIds;
        return this;
    }

    public InlineKeyboardButton asButton(LocalDate currentDate) {
        var monthName = currentDate.plusMonths(monthOffset).getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());
        if (monthOffset < 0)
            return asButton(String.format("◀️ В %s", monthName));
        return asButton(String.format("В %s ▶️", monthName));
    }
}
