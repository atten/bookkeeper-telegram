package bookkeeper.telegram;


import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.repositories.AccountTransactionRepository;
import bookkeeper.services.repositories.MerchantExpenditureRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.services.matchers.ExpenditureMatcherByMerchant;
import bookkeeper.telegram.scenarios.refine.*;
import bookkeeper.telegram.scenarios.review.SlashShowMonthlyExpensesHandler;
import bookkeeper.telegram.scenarios.store.tinkoff.TinkoffSmsHandler;
import bookkeeper.telegram.shared.AbstractHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Bot {
    private final TelegramBot bot;
    private final List<AbstractHandler> handlers;
    private final EntityManager entityManager;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Bot(Config config) {
        bot = new TelegramBot(config.botToken());
        entityManager = config.entityManager();

        var telegramUserRepository = new TelegramUserRepository(entityManager);
        var merchantExpenditureRepository = new MerchantExpenditureRepository(entityManager);
        var accountRepository = new AccountRepository(entityManager);
        var transactionRepository = new AccountTransactionRepository(entityManager);

        var merchantBalanceCategoryMatcher = new ExpenditureMatcherByMerchant(merchantExpenditureRepository);

        handlers = List.of(
            new LoggingHandler(bot, telegramUserRepository),
            new SlashStartHandler(bot, telegramUserRepository),
            new SlashClearAssociationsHandler(bot, telegramUserRepository, merchantExpenditureRepository),
            new SlashShowMonthlyExpensesHandler(bot, telegramUserRepository, accountRepository, transactionRepository),
            new RefineMonthlyTransactionsCallbackHandler(bot, telegramUserRepository, transactionRepository),
            new TinkoffSmsHandler(bot, telegramUserRepository, accountRepository, transactionRepository, merchantBalanceCategoryMatcher),
            new SelectExpenditureCallbackHandler(bot, telegramUserRepository),
            new AssignExpenditureCallbackHandler(bot, telegramUserRepository, transactionRepository, merchantExpenditureRepository),
            new MerchantExpenditureRemoveCallbackHandler(bot, telegramUserRepository, merchantExpenditureRepository),
            new TransactionApproveCallbackHandler(bot, telegramUserRepository, transactionRepository),
            new TransactionApproveBulkCallbackHandler(bot, telegramUserRepository, transactionRepository),
            new TransactionEditBulkCallbackHandler(bot, telegramUserRepository, transactionRepository),
            new UnknownInputHandler(bot, telegramUserRepository)
        );
    }

    /**
     * Run the telegram bot in a long-polling mode.
     */
    public void listen() {
        bot.setUpdatesListener(updates -> {
            for (var update : updates) {
                processUpdate(update);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
        logger.info("Start listening...");
    }

    /**
     * Process a single incoming request through chain of handlers.
     * Whole procedure is wrapped into transaction.
     */
    private void processUpdate(Update update) {
        entityManager.getTransaction().begin();

        for (AbstractHandler handler : handlers) {
            Boolean processed = handler.handle(update);
            if (processed)
                break;
        }

        entityManager.getTransaction().commit();
    }
}
