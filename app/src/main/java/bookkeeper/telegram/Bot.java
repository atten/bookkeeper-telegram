package bookkeeper.telegram;

import bookkeeper.dao.repository.TelegramUserRepository;
import bookkeeper.exception.HandlerInterruptException;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;
import bookkeeper.service.telegram.StringShortenerCache;
import bookkeeper.service.telegram.StringUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.utility.BotUtils;
import com.sun.net.httpserver.HttpServer;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
class Bot {
    private final TelegramBot bot;
    private final EntityManager entityManager;
    private final List<AbstractHandler> handlers;
    private final TelegramUserRepository userRepository;
    private final StringShortenerCache stringShortenerCache;
    private final HttpServer webhookServer;

    @Inject
    Bot(
        TelegramBot telegramBot,
        EntityManager entityManager,
        Set<AbstractHandler> handlers,
        TelegramUserRepository userRepository,
        StringShortenerCache stringShortenerCache,
        Optional<HttpServer> webhookServer
    ) {
        this.bot = telegramBot;
        this.entityManager = entityManager;
        this.stringShortenerCache = stringShortenerCache;
        this.handlers = handlers
            .stream()
            .sorted(Comparator.comparing(AbstractHandler::getPriority))
            .toList();
        this.userRepository = userRepository;
        this.webhookServer = webhookServer.orElse(null);
        log.info("{} handlers loaded", this.handlers.size());
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
     * Run the telegram bot in a long-polling or webhook mode depending on settings.
     */
    void listen() {
        if (webhookServer == null) {
            // Run the telegram bot in a long-polling mode.
            bot.setUpdatesListener(updates -> {
                updates.forEach(this::processUpdate);
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            });
            log.info("Start listening via API...");
        } else {
            webhookServer.createContext("/", httpExchange -> {
                var update = BotUtils.parseUpdate(new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8));
                if (update == null) {
                    httpExchange.sendResponseHeaders(400, 0);
                } else if (processUpdate(update)) {
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    httpExchange.sendResponseHeaders(500, 0);
                }
                httpExchange.close();
            });
            webhookServer.start();
            log.info("Start listening webhook on port {}...", webhookServer.getAddress().getPort());
        }
    }

    private void setupCommands() {
        var commandsRequest = new SetMyCommands(
            new BotCommand("annual", "Готовой отчёт"),
            new BotCommand("assets", "Баланс счетов"),
            new BotCommand("expenses", "Расходы за месяц"),
            new BotCommand("check_residues", "Сверить остатки"),
            new BotCommand("new_account", "Создать счёт"),
            new BotCommand("new_transfer", "Создать перевод между счетами"),
            new BotCommand("accounts", "Редактировать счета")
        );
        var result = bot.execute(commandsRequest);
        var resultVerbose = result.description() != null ? result.description() : "OK";
        log.info("Set commands... {}", resultVerbose);
    }

    /**
     * Process a single incoming request through chain of handlers.
     * Whole procedure is wrapped into transaction.
     */
    public Boolean processUpdate(Update update) {
        entityManager.getTransaction().begin();

        var request = new Request(update, bot, userRepository, stringShortenerCache);
        request.setupUser();
        Boolean processed = false;

        for (AbstractHandler handler : handlers) {
            try {
                processed = handler.handle(request);
            } catch (HandlerInterruptException e) {
                processed = true;
                log.warn(e.toString());
                request.sendMessage("%s Ошибка: `%s`".formatted(StringUtils.ICON_ERROR, e.getLocalizedMessage()));
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
        return processed;
    }
}
