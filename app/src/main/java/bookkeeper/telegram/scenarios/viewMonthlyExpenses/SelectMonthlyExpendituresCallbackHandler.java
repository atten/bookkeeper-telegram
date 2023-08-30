package bookkeeper.telegram.scenarios.viewMonthlyExpenses;

import bookkeeper.entities.TelegramUser;
import bookkeeper.enums.Expenditure;
import bookkeeper.services.repositories.AccountTransactionRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.telegram.scenarios.editTransactions.EditTransactionBulkCallback;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.CallbackMessageRegistry;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * Scenario: user browses monthly expenses.
 */
public class SelectMonthlyExpendituresCallbackHandler extends AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    public SelectMonthlyExpendituresCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountTransactionRepository transactionRepository) {
        super(bot, telegramUserRepository);
        this.transactionRepository = transactionRepository;
    }

    /**
     * Display expenditures selector to browse monthly transactions
     */
    @Override
    public Boolean handle(Update update) {
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(update);
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof SelectMonthlyExpendituresCallback))
            return false;

        var cm = (SelectMonthlyExpendituresCallback) callbackMessage.get();

        var user = getTelegramUser(update);
        editMessage(update, getResponseKeyboard(cm.getMonthOffset(), user));

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
