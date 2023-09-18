package bookkeeper.telegram;


import bookkeeper.service.repository.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.Request;
import bookkeeper.telegram.shared.exception.SkipHandlerException;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Slf4j
class Bot {
    private final TelegramBot bot;
    private final EntityManager entityManager;
    private final List<AbstractHandler> handlers;
    private final TelegramUserRepository userRepository;

    @Inject
    Bot(
        TelegramBot telegramBot,
        EntityManager entityManager,
        Set<AbstractHandler> handlers,
        TelegramUserRepository userRepository) {
        this.bot = telegramBot;
        this.entityManager = entityManager;
        this.handlers = handlers
            .stream()
            .sorted(Comparator.comparing(AbstractHandler::getPriority))
            .toList();
        this.userRepository = userRepository;
        log.info(String.format("%s handlers loaded", this.handlers.size()));
    }

    void notifyStartup(int telegramUserId) {
        var text = "New version deployed!";
        var message = new SendMessage(telegramUserId, text).parseMode(ParseMode.Markdown);
        var result = bot.execute(message);
        var resultVerbose = result.description() != null ? result.description() : "OK";

        log.info("{} -> {} ({})", text, telegramUserId, resultVerbose);
    }

    /**
     * Run the telegram bot in a long-polling mode.
     */
    void listen() {
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::processUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
        log.info("Start listening...");
    }

    /**
     * Process a single incoming request through chain of handlers.
     * Whole procedure is wrapped into transaction.
     */
    private void processUpdate(Update update) {
        entityManager.getTransaction().begin();

        var request = new Request(update, bot, userRepository);
        Boolean processed = false;

        for (AbstractHandler handler : handlers) {
            try {
                processed = handler.handle(request);
            } catch (SkipHandlerException e) {
                processed = true;
                log.warn(e.toString());
                request.sendMessage(String.format("Ошибка: `%s`", e.getLocalizedMessage()));
                break;
            }
            if (processed)
                break;
        }

        if (!processed) {
            request.sendMessage("Неверная или неподдерживаемая команда, попробуйте по-другому.");
        }

        entityManager.getTransaction().commit();
    }
}
