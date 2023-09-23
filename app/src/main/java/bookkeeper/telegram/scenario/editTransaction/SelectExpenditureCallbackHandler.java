package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.enums.Expenditure;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.KeyboardUtils;
import bookkeeper.telegram.shared.Request;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import javax.inject.Inject;
import java.util.List;


/**
 * Scenario: user assigns transaction expenditure.
 */
class SelectExpenditureCallbackHandler implements AbstractHandler {

    @Inject
    SelectExpenditureCallbackHandler() {}

    /**
     * Handle "Pick Expenditure" button click: display Expenditures list for given AccountTransaction
     */
    public Boolean handle(Request request) {
        if (!(request.getCallbackMessage().orElse(null) instanceof SelectExpenditureCallback cm))
            return false;

        request.editMessage(getResponseKeyboard(cm.getTransactionId(), cm.getPendingTransactionIds()));
        return true;
    }

    private InlineKeyboardMarkup getResponseKeyboard(long transactionId, List<Long> pendingTransactionIds) {
        // prepare buttons with expenditures selector
        var buttons = Expenditure
            .enabledValues()
            .stream()
            .map(expenditure -> new AssignExpenditureCallback(transactionId, expenditure).setPendingTransactionIds(pendingTransactionIds).asButton(expenditure.getVerboseName()))
            .toList();
        return KeyboardUtils.createMarkupWithFixedColumns(buttons, 3);
    }
}
