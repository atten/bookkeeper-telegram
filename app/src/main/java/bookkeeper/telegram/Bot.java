package bookkeeper.telegram;


import bookkeeper.repositories.TelegramUserRepository;
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

        TelegramUserRepository telegramUserRepository = new TelegramUserRepository(entityManager);

        handlers = List.of(
            new LoggingHandler(bot, telegramUserRepository),
            new TinkoffSmsHandler(bot, telegramUserRepository),
            new UnknownInputHandler(bot, telegramUserRepository)
        );
    }

    /**
     * Run the telegram bot in a long-polling mode.
     */
    public void listen() {
        this.bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
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
