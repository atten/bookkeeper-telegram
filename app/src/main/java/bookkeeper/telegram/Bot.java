package bookkeeper.telegram;


import bookkeeper.service.repository.TelegramUserRepository;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;
import bookkeeper.exception.HandlerInterruptException;
import bookkeeper.service.telegram.StringUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;

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
    private final JedisPool jedisPool;

    @Inject
    Bot(
        TelegramBot telegramBot,
        EntityManager entityManager,
        Set<AbstractHandler> handlers,
        TelegramUserRepository userRepository,
        JedisPool jedisPool
    ) {
        this.bot = telegramBot;
        this.entityManager = entityManager;
        this.jedisPool = jedisPool;
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

    void setup() {
        setupCommands();
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

    private void setupCommands() {
        var commandsRequest = new SetMyCommands(
            new BotCommand("annual", "Готовой отчёт"),
            new BotCommand("assets", "Баланс счетов"),
            new BotCommand("expenses", "Расходы за месяц"),
            new BotCommand("new_account", "Создать счёт"),
            new BotCommand("new_transfer", "Создать перевод между счетами"),
            new BotCommand("accounts", "Редактировать счета"),
            new BotCommand("clear_associations", "Удалить сохранённые привязки категорий")
        );
        var result = bot.execute(commandsRequest);
        var resultVerbose = result.description() != null ? result.description() : "OK";
        log.info("Set commands... {}", resultVerbose);
    }

    /**
     * Process a single incoming request through chain of handlers.
     * Whole procedure is wrapped into transaction.
     */
    public void processUpdate(Update update) {
        entityManager.getTransaction().begin();

        var request = new Request(update, bot, userRepository, jedisPool);
        Boolean processed = false;

        for (AbstractHandler handler : handlers) {
            try {
                processed = handler.handle(request);
            } catch (HandlerInterruptException e) {
                processed = true;
                log.warn(e.toString());
                request.sendMessage(String.format("%s Ошибка: `%s`", StringUtils.ICON_ERROR, e.getLocalizedMessage()));
                break;
            }
            if (processed)
                break;
        }

        if (!processed) {
            request.sendMessage("Неверная или неподдерживаемая команда, попробуйте по-другому.");
        }

        entityManager.getTransaction().commit();
        entityManager.clear();
    }
}
