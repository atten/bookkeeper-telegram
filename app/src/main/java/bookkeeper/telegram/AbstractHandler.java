package bookkeeper.telegram;

import bookkeeper.entities.TelegramUser;
import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.telegram.callbacks.CallbackMessage;
import bookkeeper.telegram.callbacks.CallbackMessageRegistry;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.request.SendMessage;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public abstract class AbstractHandler {
    private final TelegramBot bot;
    private final TelegramUserRepository telegramUserRepository;
    private final CallbackMessageRegistry callbackMessageRegistry = new CallbackMessageRegistry();
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    AbstractHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository) {
        this.bot = bot;
        this.telegramUserRepository = telegramUserRepository;
    }

    abstract Boolean handle(Update update);

    protected void sendMessage(Update update, String text, @Nullable Keyboard keyboard) {
        var telegramUser = getTelegramUser(update);

        var message = new SendMessage(telegramUser.getTelegramId(), text);
        if (keyboard != null)
            message = message.replyMarkup(keyboard);

        var result = bot.execute(message);
        var resultVerbose = result.description() != null ? result.description() : "OK";

        logger.info("{} {} <- {} ({})", new Date(), telegramUser, text, resultVerbose);
    }

    protected void sendMessage(Update update, String text) {
        sendMessage(update, text, null);
    }

    protected TelegramUser getTelegramUser(Update update) {
        var telegramUser = telegramUserRepository.getOrCreate(getUser(update));
        telegramUserRepository.updateLastAccess(telegramUser);
        return telegramUser;
    }

    protected CallbackMessage getCallbackMessage(Update update) {
        return callbackMessageRegistry.getCallbackMessage(update);
    }

    protected String getTextPlural(Integer count, String single, String few, String many) {
        if (count == 0)
            return many;
        if (count % 10 == 1)
            return single;
        if (count % 10 <= 4)
            return few;
        return many;
    }

    private User getUser(Update update) {
        if (update.message() != null)
            return update.message().from();
        return update.callbackQuery().from();
    }
}
