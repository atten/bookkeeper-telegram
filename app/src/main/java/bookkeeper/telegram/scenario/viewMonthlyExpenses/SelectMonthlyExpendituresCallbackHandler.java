package bookkeeper.telegram.scenario.viewMonthlyExpenses;

import bookkeeper.dao.AccountTransactionRepository;
import bookkeeper.dao.entity.TelegramUser;
import bookkeeper.enums.Expenditure;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.KeyboardUtils;
import bookkeeper.service.telegram.Request;
import bookkeeper.telegram.scenario.editTransaction.EditTransactionBulkCallback;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Scenario: user browses monthly expenses.
 */
class SelectMonthlyExpendituresCallbackHandler implements AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    @Inject
    SelectMonthlyExpendituresCallbackHandler(AccountTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Display expenditures selector to browse monthly transactions
     */
    public Boolean handle(Request request) {
        if (!(request.getCallbackMessage().orElse(null) instanceof SelectMonthlyExpendituresCallback cm))
            return false;

        var user = request.getTelegramUser();
        request.editMessage(getResponseKeyboard(cm.getMonthOffset(), user));

        return true;
    }

    private InlineKeyboardMarkup getResponseKeyboard(int monthOffset, TelegramUser user) {
        Map<Expenditure, List<Long>> idsByExpenditure = new LinkedHashMap<>();

        Expenditure.enabledValues()
            .forEach(expenditure -> {
                var ids = transactionRepository.findIds(expenditure, monthOffset, user);
                if (!ids.isEmpty())
                    idsByExpenditure.put(expenditure, ids);
            });

        // prepare buttons with expenditures selector
        var buttons = idsByExpenditure.entrySet().stream()
            .map(entry -> new EditTransactionBulkCallback(entry.getValue()).asButton(entry.getKey().getVerboseName()))
            .toList();

        return KeyboardUtils.createMarkupWithFixedColumns(buttons, 3)
            // button which returns to monthly expenses menu
            .addRow(new ViewMonthlyExpensesWithOffsetCallback(monthOffset).asButton("Назад"));
    }
}
