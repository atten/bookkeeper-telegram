package bookkeeper.telegram;


import bookkeeper.services.repositories.*;
import bookkeeper.services.matchers.ExpenditureMatcherByMerchant;
import bookkeeper.telegram.scenarios.addAccount.AddAccountHandler;
import bookkeeper.telegram.scenarios.addTransfer.AddTransferHandler;
import bookkeeper.telegram.scenarios.editTransactions.*;
import bookkeeper.telegram.scenarios.viewAssets.ViewAssetsHandler;
import bookkeeper.telegram.scenarios.viewMonthlyExpenses.SelectMonthlyExpendituresCallbackHandler;
import bookkeeper.telegram.scenarios.viewMonthlyExpenses.ViewMonthlyExpensesHandler;
import bookkeeper.telegram.scenarios.addTransactions.freehand.FreehandRecordHandler;
import bookkeeper.telegram.scenarios.addTransactions.tinkoff.TinkoffSmsHandler;
import bookkeeper.telegram.shared.AbstractHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class Bot {
    private static final TelegramBot bot = new TelegramBot(Config.botToken());
    private static final EntityManager entityManager = Config.entityManager();
    private static final Logger logger = LoggerFactory.getLogger(Bot.class);
    private final List<AbstractHandler> handlers;

    Bot() {
        var telegramUserRepository = new TelegramUserRepository(entityManager);
        var merchantExpenditureRepository = new MerchantExpenditureRepository(entityManager);
        var accountRepository = new AccountRepository(entityManager);
        var transactionRepository = new AccountTransactionRepository(entityManager);
        var transferRepository = new AccountTransferRepository(entityManager);

        var expenditureMatcherByMerchant = new ExpenditureMatcherByMerchant(merchantExpenditureRepository);

        handlers = List.of(
            // 0. logging
            new LoggingHandler(bot, telegramUserRepository),

            // 1. configuration
            new LocaleHandler(bot, telegramUserRepository),

            // 2. commands
            new AddAccountHandler(bot, telegramUserRepository, accountRepository),
            new AddTransferHandler(bot, telegramUserRepository, accountRepository, transferRepository),
            new SlashStartHandler(bot, telegramUserRepository),
            new SlashClearAssociationsHandler(bot, telegramUserRepository, merchantExpenditureRepository),
            new ViewMonthlyExpensesHandler(bot, telegramUserRepository, accountRepository, transactionRepository),
            new ViewAssetsHandler(bot, telegramUserRepository, accountRepository, transactionRepository, transferRepository),

            // 3. text input and callbacks
            new SelectMonthlyExpendituresCallbackHandler(bot, telegramUserRepository, transactionRepository),
            new TinkoffSmsHandler(bot, telegramUserRepository, accountRepository, transactionRepository, expenditureMatcherByMerchant),
            new FreehandRecordHandler(bot, telegramUserRepository, accountRepository, transactionRepository, expenditureMatcherByMerchant),
            new SelectExpenditureCallbackHandler(bot, telegramUserRepository),
            new AssignExpenditureCallbackHandler(bot, telegramUserRepository, transactionRepository, merchantExpenditureRepository),
            new RemoveTransactionCallbackHandler(bot, telegramUserRepository, transactionRepository),
            new RemoveMerchantExpenditureCallbackHandler(bot, telegramUserRepository, merchantExpenditureRepository),
            new ApproveTransactionCallbackHandler(bot, telegramUserRepository, transactionRepository),
            new ApproveTransactionBulkCallbackHandler(bot, telegramUserRepository, transactionRepository),
            new EditTransactionBulkCallbackHandler(bot, telegramUserRepository, transactionRepository),
            new ShiftTransactionMonthCallbackHandler(bot, telegramUserRepository, transactionRepository),

            // 999. closing condition
            new UnknownInputHandler(bot, telegramUserRepository)
        );
    }

    void notifyStartup(int telegramUserId) {
        var text = "New version deployed!";
        var message = new SendMessage(telegramUserId, text).parseMode(ParseMode.Markdown);
        var result = bot.execute(message);
        var resultVerbose = result.description() != null ? result.description() : "OK";

        logger.info("{} -> {} ({})", text, telegramUserId, resultVerbose);
    }

    /**
     * Run the telegram bot in a long-polling mode.
     */
    void listen() {
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
