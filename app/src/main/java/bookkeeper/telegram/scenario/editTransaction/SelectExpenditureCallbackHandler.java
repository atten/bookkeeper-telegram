package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.enums.Expenditure;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.KeyboardUtils;
import bookkeeper.service.telegram.Request;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import javax.inject.Inject;
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

        request.editMessage(getResponseKeyboard(cm));
        return true;
    }

    private InlineKeyboardMarkup getResponseKeyboard(SelectExpenditureCallback callback) {
        // buttons with expenditures selector
        var buttons = Expenditure
            .enabledValues()
            .stream()
            .map(expenditure -> new AssignExpenditureCallback(callback.getTransactionId(), expenditure)
                .setTransactionIds(callback.getAllTransactionIds(), callback.getPendingTransactionIds())
                .asButton(expenditure.getVerboseName()))
            .toList();

        // button which returns to transaction edit message
        var pendingTransactionIds = Stream.concat(Stream.of(callback.getTransactionId()), callback.getPendingTransactionIds().stream()).toList();
        var backButton = new EditTransactionBulkCallback(callback.getAllTransactionIds(), pendingTransactionIds).asButton("Назад");

        return KeyboardUtils.createMarkupWithFixedColumns(buttons, 3)
            .addRow(backButton);
    }
}
