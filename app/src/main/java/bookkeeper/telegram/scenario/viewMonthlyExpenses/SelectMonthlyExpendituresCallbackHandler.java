package bookkeeper.telegram.scenario.viewMonthlyExpenses;

import bookkeeper.entity.TelegramUser;
import bookkeeper.enums.Expenditure;
import bookkeeper.service.registry.CallbackMessageRegistry;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.telegram.scenario.editTransaction.EditTransactionBulkCallback;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.Request;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


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
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(request.getUpdate());
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof SelectMonthlyExpendituresCallback cm))
            return false;

        var user = request.getTelegramUser();
        request.editMessage(getResponseKeyboard(cm.getMonthOffset(), user));

        return true;
    }

    private InlineKeyboardMarkup getResponseKeyboard(int monthOffset, TelegramUser user) {
        var kb = new InlineKeyboardMarkup();
        var groupBy = 3;
        AtomicInteger index = new AtomicInteger(0);

        Map<Expenditure, List<Long>> idsByExpenditure = new LinkedHashMap<>();

        Expenditure.enabledValues()
            .forEach(expenditure -> {
                var ids = transactionRepository.findIds(expenditure, monthOffset, user);
                if (!ids.isEmpty())
                    idsByExpenditure.put(expenditure, ids);
            });

        idsByExpenditure.entrySet().stream()
            .map(entry ->
                // prepare buttons with expenditures selector
                new EditTransactionBulkCallback(entry.getValue()).asButton(entry.getKey().getVerboseName())
            ).collect(
                // split to N map items each contains a list of 3 buttons
                Collectors.groupingBy(i -> index.getAndIncrement() / groupBy)
            ).values().forEach ((inlineKeyboardButtons) ->
                // append keyboard rows
                kb.addRow(inlineKeyboardButtons.toArray(InlineKeyboardButton[]::new))
            );

        // button which returns to monthly expenses menu
        kb.addRow(new ViewMonthlyExpensesWithOffsetCallback(monthOffset).asButton("Назад"));
        return kb;
    }
}
