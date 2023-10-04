package bookkeeper.service.telegram;

import bookkeeper.entity.TelegramUser;
import bookkeeper.service.repository.TelegramUserRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

import static bookkeeper.service.telegram.StringUtils.cleanString;

@Slf4j
public class Request {
    private final Update update;
    private final TelegramBot bot;
    private final TelegramUserRepository telegramUserRepository;
    private final CallbackMessageRegistry callbackMessageRegistry;

    public Request(Update update, TelegramBot bot, TelegramUserRepository telegramUserRepository, JedisPool jedisPool) {
        this.update = update;
        this.bot = bot;
        this.telegramUserRepository = telegramUserRepository;
        this.callbackMessageRegistry = new CallbackMessageRegistry(jedisPool);
    }

    public TelegramUser getTelegramUser() {
        var telegramUser = telegramUserRepository.getOrCreate(getUser(update));
        telegramUserRepository.updateLastAccess(telegramUser);
        return telegramUser;
    }

    public String getMessageText() {
        if (update.message() == null)
            return "";
        if (update.message().text() == null)
            return "";
        return cleanString(update.message().text());
    }

    private User getUser(Update update) {
        if (update.message() != null)
            return update.message().from();
        if (update.callbackQuery() != null)
            return update.callbackQuery().from();
        return update.editedMessage().from();
    }

    public Optional<CallbackMessage> getCallbackMessage() {
        return callbackMessageRegistry.getCallbackMessage(update.callbackQuery());
    }

    /**
     * Retrieve callback data from Nth button of replied message (if present).
     * Is used to extract context when user replies to message with buttons.
     */
    public Optional<CallbackMessage> getCallbackMessageFromReply(int index) {
        if (getReplyToMessage().isEmpty())
            return Optional.empty();

        var button = Arrays
            .stream(getReplyToMessage().get().replyMarkup().inlineKeyboard())
            .flatMap(Arrays::stream)
            .toList()
            .get(index);
        return callbackMessageRegistry.getCallbackMessage(button.callbackData());
    }

    public Optional<Message> getReplyToMessage() {
        if (update.message() != null)
            return Optional.ofNullable(update.message().replyToMessage());
        return Optional.empty();
    }

    public void sendMessage(String text, Keyboard keyboard) {
        sendMessage(text, keyboard, false);
    }

    public void sendMessage(String text) {
        sendMessage(text, null, false);
    }

    public void replyMessage(String text, Keyboard keyboard) {
        sendMessage(text, keyboard, true);
    }

    public void editMessage(String text, InlineKeyboardMarkup keyboard, int messageId) {
        editMessagePrivate(text, keyboard, messageId);
    }

    public void editMessage(String text, InlineKeyboardMarkup keyboard) {
        editMessagePrivate(text, keyboard, getMessageId());
    }

    public void editMessage(String text) {
        editMessagePrivate(text, null, getMessageId());
    }

    public void editMessage(InlineKeyboardMarkup keyboard) {
        editMessagePrivate(null, keyboard, getMessageId());
    }

    public void deleteMessage() {
        var deleteRequest = new DeleteMessage(getChatId(), getMessageId());
        var result = bot.execute(deleteRequest);

        var resultVerbose = result.description() != null ? result.description() : "OK";
        log.info("Delete message id={} by {} ({})", getMessageId(), getTelegramUser(), resultVerbose);
    }

    public String toString() {
        if (update.message() != null)
            return update.message().text();
        if (update.callbackQuery() != null)
            return String.format("(callback): %s", update.callbackQuery().data());
        return String.format("(edited): %s", update.editedMessage().text());
    }

    private static Optional<ParseMode> detectParseMode(String message) {
        if (message.contains("</") && message.contains(">"))
            return Optional.of(ParseMode.HTML);
        if (message.contains("`") || message.contains("*"))
            return Optional.of(ParseMode.Markdown);
        // MarkdownV2 is impractical because requires escaping of non-pairing symbols like '|', '-' etc
        return Optional.empty();
    }

    private void sendMessage(String text, @Nullable Keyboard keyboard, Boolean reply) {
        var telegramUser = getTelegramUser();
        var parseMode = detectParseMode(text);
        var keyboardVerbose = "";

        var message = new SendMessage(telegramUser.getTelegramId(), text);

        if (parseMode.isPresent())
            message = message.parseMode(parseMode.get());

        if (keyboard != null) {
            if (keyboard instanceof InlineKeyboardMarkup kb) {
                callbackMessageRegistry.prepareKeyboardBeforeSend(kb);
                keyboardVerbose = KeyboardUtils.getInlineKeyboardVerboseString(kb);
            }
            message = message.replyMarkup(keyboard);
        }

        if (reply)
            message = message.replyToMessageId(getMessageId());

        var result = bot.execute(message);
        var resultVerbose = result.description() != null ? result.description() : "OK";

        log.info("{}{} -> {} ({})", text, keyboardVerbose, telegramUser, resultVerbose);
    }

    private void editMessagePrivate(@Nullable String text, @Nullable InlineKeyboardMarkup keyboard, int messageId) {
        var keyboardVerbose = "";

        if (keyboard != null) {
            callbackMessageRegistry.prepareKeyboardBeforeSend(keyboard);
            keyboardVerbose = KeyboardUtils.getInlineKeyboardVerboseString(keyboard);
        }

        BaseResponse result;
        if (text == null && keyboard != null) {
            var message = new EditMessageReplyMarkup(getChatId(), messageId).replyMarkup(keyboard);
            result = bot.execute(message);
        }
        else if (text != null) {
            var message = new EditMessageText(getChatId(), messageId, text);
            var parseMode = detectParseMode(text);

            if (parseMode.isPresent())
                message = message.parseMode(parseMode.get());

            if (keyboard != null) {
                message = message.replyMarkup(keyboard);
            }

            result = bot.execute(message);
        } else {
            throw new RuntimeException();
        }

        var resultVerbose = result.description() != null ? result.description() : "OK";
        var testVerbose = text != null ? text : "(empty)";
        log.info("{}{} -> {} ({})", testVerbose, keyboardVerbose, getTelegramUser(), resultVerbose);
    }

    private long getChatId() {
        if (update.message() != null)
            return update.message().chat().id();
        if (update.callbackQuery() != null)
            return update.callbackQuery().message().chat().id();
        return update.editedMessage().chat().id();
    }

    private int getMessageId() {
        if (update.message() != null)
            return update.message().messageId();
        return update.callbackQuery().message().messageId();
    }
}
