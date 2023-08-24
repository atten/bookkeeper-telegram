package bookkeeper.telegram.shared;

import bookkeeper.telegram.scenarios.edit.*;
import com.pengrad.telegrambot.model.Update;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;

class CallbackMessageRegistry {
    private final StringShortener shortener = StringShortener.FOR_TELEGRAM_CALLBACK;

    private final List<CallbackMessage> callbackMessages = List.of(
        new SelectExpenditureCallback(),
        new AssignExpenditureCallback(),
        new MerchantExpenditureRemoveCallback(),
        new RefineMonthlyTransactionsCallback(),
        new TransactionApproveCallback(),
        new TransactionApproveBulkCallback(),
        new TransactionEditBulkCallback()
    );

    @Nullable
    CallbackMessage getCallbackMessage(Update update) {
        if (update.callbackQuery() == null)
            return null;

        var callbackData = shortener.unshrink(update.callbackQuery().data());

        return callbackMessages.stream()
                .map(callbackMessage -> {
                    try {
                        return callbackMessage.parse(callbackData);
                    } catch (ParseException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .reduce((callbackMessage, callbackMessage2) -> {
                    throw new RuntimeException(String.format("Expected a single CallbackMessage match, got %s, %s", callbackMessage, callbackMessage2));
                })
                .orElse(null);
    }
}
