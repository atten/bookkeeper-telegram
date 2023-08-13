package bookkeeper.telegram.callbacks;

import com.pengrad.telegrambot.model.Update;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;

public class CallbackMessageRegistry {
    List<CallbackMessage> callbackMessages = List.of(
        new ExpenditurePickCallback(),
        new ExpenditureAssignCallback(),
        new TransactionApproveCallback(),
        new TransactionApproveBulkCallback(),
        new TransactionEditBulkCallback()
    );

    @Nullable public CallbackMessage getCallbackMessage(Update update) {
        if (update.callbackQuery() == null)
            return null;

        return callbackMessages.stream()
                .map(callbackMessage -> {
                    try {
                        return callbackMessage.parse(update.callbackQuery().data());
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
