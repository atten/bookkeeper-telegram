package bookkeeper.telegram;

import bookkeeper.entities.TelegramUser;
import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.telegram.callbacks.CallbackMessage;
import bookkeeper.telegram.callbacks.CallbackMessageRegistry;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

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

    protected void sendMessage(Update update, String text, @Nullable Keyboard keyboard, Boolean reply) {
        var telegramUser = getTelegramUser(update);

        var message = new SendMessage(telegramUser.getTelegramId(), text).parseMode(ParseMode.Markdown);

        if (keyboard != null)
            message = message.replyMarkup(keyboard);

        if (reply)
            message = message.replyToMessageId(getMessageId(update));

        var result = bot.execute(message);
        var resultVerbose = result.description() != null ? result.description() : "OK";

        logger.info("{} {} <- {} ({})", Instant.now(), telegramUser, text, resultVerbose);
    }

    protected void sendMessage(Update update, String text, Keyboard keyboard) {
        sendMessage(update, text, keyboard, false);
    }

    protected void sendMessage(Update update, String text) {
        sendMessage(update, text, null, false);
    }

    protected void editMessage(Update update, @Nullable String text, @Nullable InlineKeyboardMarkup keyboard) {
        var telegramUser = getTelegramUser(update);

        BaseResponse result;
        if (text == null && keyboard != null) {
            var message = new EditMessageReplyMarkup(getChatId(update), getMessageId(update)).replyMarkup(keyboard);
            result = bot.execute(message);
        }
        else if (text != null) {
            var message = new EditMessageText(getChatId(update), getMessageId(update), text);

            if (keyboard != null)
                message = message.replyMarkup(keyboard);

            result = bot.execute(message);
        } else {
            throw new RuntimeException();
        }

        var resultVerbose = result.description() != null ? result.description() : "OK";
        logger.info("{} {} <- {} ({})", Instant.now(), telegramUser, keyboard, resultVerbose);
    }

    protected void editMessage(Update update, String text) {
        editMessage(update, text, null);
    }

    protected void editMessage(Update update, InlineKeyboardMarkup keyboard) {
        editMessage(update, null, keyboard);
    }

    protected TelegramUser getTelegramUser(Update update) {
        var telegramUser = telegramUserRepository.getOrCreate(getUser(update));
        telegramUserRepository.updateLastAccess(telegramUser);
        return telegramUser;
    }

    protected CallbackMessage getCallbackMessage(Update update) {
        return callbackMessageRegistry.getCallbackMessage(update);
    }

    private User getUser(Update update) {
        if (update.message() != null)
            return update.message().from();
        return update.callbackQuery().from();
    }

    private long getChatId(Update update) {
        if (update.message() != null)
            return update.message().chat().id();
        return update.callbackQuery().message().chat().id();
    }

    private int getMessageId(Update update) {
        if (update.message() != null)
            return update.message().messageId();
        return update.callbackQuery().message().messageId();
    }
}
