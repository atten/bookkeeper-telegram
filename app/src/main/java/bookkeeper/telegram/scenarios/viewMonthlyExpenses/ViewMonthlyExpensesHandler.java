package bookkeeper.telegram.scenarios.viewMonthlyExpenses;

import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.repositories.AccountTransactionRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.CallbackMessageRegistry;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.util.Objects;


/**
 * Scenario: user requests monthly expense statistics.
 */
public class ViewMonthlyExpensesHandler extends AbstractHandler {
    private final MonthlyExpensesResponseFactory monthlyExpensesResponseFactory;

    public ViewMonthlyExpensesHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountRepository accountRepository, AccountTransactionRepository transactionRepository) {
        super(bot, telegramUserRepository);
        this.monthlyExpensesResponseFactory = new MonthlyExpensesResponseFactory(accountRepository, transactionRepository);
    }

    /**
     * Display monthly expense statistics
     */
    @Override
    public Boolean handle(Update update) {
        return handleCallbackMessage(update) || handleSlashExpenses(update);
    }

    private Boolean handleCallbackMessage(Update update) {
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(update);
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof ViewMonthlyExpensesWithOffsetCallback))
            return false;

        var cm = (ViewMonthlyExpensesWithOffsetCallback) callbackMessage.get();
        var user = getTelegramUser(update);
        var message = monthlyExpensesResponseFactory.getMonthlyExpenses(user, cm.getMonthOffset());
        var keyboard = MonthlyExpensesResponseFactory.getMonthlyExpensesKeyboard(cm.getMonthOffset());
        editMessage(update, message, keyboard);
        return true;
    }

    private Boolean handleSlashExpenses(Update update) {
        if (update.message() == null || !Objects.equals(update.message().text(), "/expenses"))
            return false;

        var user = getTelegramUser(update);
        var message = monthlyExpensesResponseFactory.getMonthlyExpenses(user, 0);
        var keyboard = MonthlyExpensesResponseFactory.getMonthlyExpensesKeyboard(0);
        sendMessage(update, message, keyboard);
        return true;
    }
}
