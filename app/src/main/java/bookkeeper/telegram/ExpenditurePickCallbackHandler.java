package bookkeeper.telegram;

import bookkeeper.enums.Expenditure;
import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.telegram.callbacks.ExpenditureAssignCallback;
import bookkeeper.telegram.callbacks.ExpenditurePickCallback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * Scenario: user assigns transaction expenditure.
 */
public class ExpenditurePickCallbackHandler extends AbstractHandler {

    ExpenditurePickCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository) {
        super(bot, telegramUserRepository);
    }

    /**
     * Handle "Pick Expenditure" button click: display Expenditures list for given AccountTransaction
     */
    @Override
    Boolean handle(Update update) {
        var callbackMessage = getCallbackMessage(update);
        if (!(callbackMessage instanceof ExpenditurePickCallback))
            return false;

        var cm = ((ExpenditurePickCallback) callbackMessage);
        editMessage(update, getResponseKeyboard(cm.getTransactionId(), cm.getPendingTransactionIds()));
        return true;
    }

    private InlineKeyboardMarkup getResponseKeyboard(long transactionId, List<Long> pendingTransactionIds) {
        var kb = new InlineKeyboardMarkup();
        var groupBy = 3;
        AtomicInteger index = new AtomicInteger(0);

        Arrays.stream(Expenditure.values())
            .map(expenditure ->
                // prepare buttons with expenditures selector
                new ExpenditureAssignCallback(transactionId, expenditure).setPendingTransactionIds(pendingTransactionIds).asButton(expenditure.getName())
            ).collect(
                // split to N map items each contains a list of 3 buttons
                Collectors.groupingBy(i -> index.getAndIncrement() / groupBy)
            ).values().forEach ((inlineKeyboardButtons) ->
                // append keyboard rows
                kb.addRow(inlineKeyboardButtons.toArray(InlineKeyboardButton[]::new))
            );
        return kb;
    }
}
