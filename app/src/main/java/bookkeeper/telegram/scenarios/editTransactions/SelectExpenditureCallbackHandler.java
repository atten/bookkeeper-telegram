package bookkeeper.telegram.scenarios.editTransactions;

import bookkeeper.enums.Expenditure;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.CallbackMessageRegistry;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * Scenario: user assigns transaction expenditure.
 */
public class SelectExpenditureCallbackHandler extends AbstractHandler {

    public SelectExpenditureCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository) {
        super(bot, telegramUserRepository);
    }

    /**
     * Handle "Pick Expenditure" button click: display Expenditures list for given AccountTransaction
     */
    @Override
    public Boolean handle(Update update) {
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(update);
        if (!(callbackMessage instanceof SelectExpenditureCallback))
            return false;

        var cm = ((SelectExpenditureCallback) callbackMessage);
        editMessage(update, getResponseKeyboard(cm.getTransactionId(), cm.getPendingTransactionIds()));
        return true;
    }

    private InlineKeyboardMarkup getResponseKeyboard(long transactionId, List<Long> pendingTransactionIds) {
        var kb = new InlineKeyboardMarkup();
        var groupBy = 3;
        AtomicInteger index = new AtomicInteger(0);

        Expenditure.enabledValues().stream()
            .map(expenditure ->
                // prepare buttons with expenditures selector
                new AssignExpenditureCallback(transactionId, expenditure).setPendingTransactionIds(pendingTransactionIds).asButton(expenditure.getVerboseName())
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
