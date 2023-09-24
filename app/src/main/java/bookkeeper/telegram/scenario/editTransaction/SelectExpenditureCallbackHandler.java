package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.enums.Expenditure;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.KeyboardUtils;
import bookkeeper.telegram.shared.Request;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Stream;


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
        // buttons with expenditures selector
        var buttons = Expenditure
            .enabledValues()
            .stream()
            .map(expenditure -> new AssignExpenditureCallback(transactionId, expenditure).setPendingTransactionIds(pendingTransactionIds).asButton(expenditure.getVerboseName()))
            .toList();

        // button which returns to transaction edit message
        var allTransactionIds = Stream.concat(Stream.of(transactionId), pendingTransactionIds.stream()).toList();
        var backButton = new EditTransactionBulkCallback(allTransactionIds).asButton("Назад");

        return KeyboardUtils.createMarkupWithFixedColumns(buttons, 3)
            .addRow(backButton);
    }
}
