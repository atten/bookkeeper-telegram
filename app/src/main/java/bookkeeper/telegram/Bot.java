package bookkeeper.telegram;


import bookkeeper.repositories.AccountRepository;
import bookkeeper.repositories.AccountTransactionRepository;
import bookkeeper.repositories.MerchantExpenditureRepository;
import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.services.matchers.shared.ExpenditureMatcherByMerchant;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Bot {
    private final TelegramBot bot;
    private final List<AbstractHandler> handlers;
    // there's only one instance of database writer (the bot itself), so we can use a single persistence context throughout runtime
    private final EntityManager entityManager = Persistence.createEntityManagerFactory("default").createEntityManager();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Bot(String token) {
        bot = new TelegramBot(token);

        var telegramUserRepository = new TelegramUserRepository(entityManager);
        var merchantExpenditureRepository = new MerchantExpenditureRepository(entityManager);
        var accountRepository = new AccountRepository(entityManager);
        var transactionRepository = new AccountTransactionRepository(entityManager);

        var merchantBalanceCategoryMatcher = new ExpenditureMatcherByMerchant(merchantExpenditureRepository);

        handlers = List.of(
            new LoggingHandler(bot, telegramUserRepository),
            new SlashStartHandler(bot, telegramUserRepository),
            new SlashClearAssociationsHandler(bot, telegramUserRepository, merchantExpenditureRepository),
            new TinkoffSmsHandler(bot, telegramUserRepository, accountRepository, transactionRepository, merchantBalanceCategoryMatcher),
            new ExpenditurePickCallbackHandler(bot, telegramUserRepository),
            new ExpenditureAssignCallbackHandler(bot, telegramUserRepository, transactionRepository, merchantExpenditureRepository),
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
