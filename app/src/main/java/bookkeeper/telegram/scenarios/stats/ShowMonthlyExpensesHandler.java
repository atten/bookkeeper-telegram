package bookkeeper.telegram.scenarios.stats;

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
public class ShowMonthlyExpensesHandler extends AbstractHandler {
    private final MonthlyExpensesResponseFactory monthlyExpensesResponseFactory;

    public ShowMonthlyExpensesHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountRepository accountRepository, AccountTransactionRepository transactionRepository) {
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
        if (!(callbackMessage instanceof ShowMonthlyExpensesWithOffsetCallback))
            return false;

        var cm = ((ShowMonthlyExpensesWithOffsetCallback) callbackMessage);
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
