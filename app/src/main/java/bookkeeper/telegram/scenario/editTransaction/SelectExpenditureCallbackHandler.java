package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.enums.Expenditure;
import bookkeeper.service.registry.CallbackMessageRegistry;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.Request;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * Scenario: user assigns transaction expenditure.
 */
class SelectExpenditureCallbackHandler implements AbstractHandler {

    @Inject
    SelectExpenditureCallbackHandler() {
    }

    /**
     * Handle "Pick Expenditure" button click: display Expenditures list for given AccountTransaction
     */
    public Boolean handle(Request request) {
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(request.getUpdate());
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof SelectExpenditureCallback cm))
            return false;

        request.editMessage(getResponseKeyboard(cm.getTransactionId(), cm.getPendingTransactionIds()));
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
