package bookkeeper.telegram.scenarios.review;

import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.repositories.AccountTransactionRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.telegram.scenarios.refine.RefineMonthlyTransactionsCallback;
import bookkeeper.telegram.shared.AbstractHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.Objects;


/**
 * Scenario: user requests monthly expense statistics.
 */
public class SlashShowMonthlyExpensesHandler extends AbstractHandler {
    private final ReviewResponseFactory reviewResponseFactory;

    public SlashShowMonthlyExpensesHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountRepository accountRepository, AccountTransactionRepository transactionRepository) {
        super(bot, telegramUserRepository);
        this.reviewResponseFactory = new ReviewResponseFactory(accountRepository, transactionRepository);
    }

    /**
     * Display monthly expense statistics
     */
    @Override
    public Boolean handle(Update update) {
        if (update.message() == null || !Objects.equals(update.message().text(), "/show_monthly_expenses"))
            return false;

        var user = getTelegramUser(update);
        var keyboard = new InlineKeyboardMarkup()
            .addRow(new RefineMonthlyTransactionsCallback().asButton("Разобрать"));

        sendMessage(update, reviewResponseFactory.getMonthlyExpenses(user, 0), keyboard);
        return true;
    }
}
