package bookkeeper.telegram.shared;

import bookkeeper.entity.TelegramUser;
import bookkeeper.service.repository.TelegramUserRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Request {
    @Getter
    private final Update update;
    private final TelegramBot bot;
    private final TelegramUserRepository telegramUserRepository;

    public Request(Update update, TelegramBot bot, TelegramUserRepository telegramUserRepository) {
        this.update = update;
        this.bot = bot;
        this.telegramUserRepository = telegramUserRepository;
    }

    public TelegramUser getTelegramUser() {
        var telegramUser = telegramUserRepository.getOrCreate(getUser(update));
        telegramUserRepository.updateLastAccess(telegramUser);
        return telegramUser;
    }

    public String getMessageText() {
        if (update.message() == null)
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

    public void sendMessage(String text, Keyboard keyboard) {
        sendMessage(text, keyboard, false);
    }

    public void sendMessage(String text) {
        sendMessage(text, null, false);
    }

    public void replyMessage(String text, Keyboard keyboard) {
        sendMessage(text, keyboard, true);
    }

    public void editMessage(String text, InlineKeyboardMarkup keyboard) {
        editMessagePrivate(text, keyboard);
    }

    public void editMessage(String text) {
        editMessage(text, null);
    }

    public void editMessage(InlineKeyboardMarkup keyboard) {
        editMessagePrivate(null, keyboard);
    }

    public String toString() {
        if (update.message() != null)
            return update.message().text();
        if (update.callbackQuery() != null)
            return String.format("(callback): %s", update.callbackQuery().data());
        return String.format("(edited): %s", update.editedMessage().text());
    }

    private static Optional<ParseMode> detectParseMode(String message) {
        if (message.contains("<") && message.contains(">"))
            return Optional.of(ParseMode.HTML);
        if (message.contains("`") || message.contains("*"))
            return Optional.of(ParseMode.Markdown);
        return Optional.empty();
    }

    /**
     * Replace non-breaking spaces with regular one.
     */
    private static String cleanString(String input) {
        return input.replaceAll(Arrays.toString(Character.toChars(160)), " ");
    }

    private static String getInlineKeyboardVerboseString(InlineKeyboardMarkup keyboard) {
        var buttonsVerbose = Arrays.stream(keyboard.inlineKeyboard()).flatMap(Stream::of).map(InlineKeyboardButton::callbackData).collect(Collectors.joining(", "));
        return String.format("[%s]", buttonsVerbose);
    }


    private void sendMessage(String text, @Nullable Keyboard keyboard, Boolean reply) {
        var telegramUser = getTelegramUser();
        var parseMode = detectParseMode(text);
        var keyboardVerbose = "";

        var message = new SendMessage(telegramUser.getTelegramId(), text);

        if (parseMode.isPresent())
            message = message.parseMode(parseMode.get());

        if (keyboard != null) {
            message = message.replyMarkup(keyboard);

            if (keyboard instanceof InlineKeyboardMarkup kb)
                keyboardVerbose = getInlineKeyboardVerboseString(kb);
        }

        if (reply)
            message = message.replyToMessageId(getMessageId());

        var result = bot.execute(message);
        var resultVerbose = result.description() != null ? result.description() : "OK";

        log.info("{}{} -> {} ({})", text, keyboardVerbose, telegramUser, resultVerbose);
    }

    private void editMessagePrivate(@Nullable String text, @Nullable InlineKeyboardMarkup keyboard) {
        var keyboardVerbose = "";

        BaseResponse result;
        if (text == null && keyboard != null) {
            var message = new EditMessageReplyMarkup(getChatId(), getMessageId()).replyMarkup(keyboard);
            result = bot.execute(message);
        }
        else if (text != null) {
            var message = new EditMessageText(getChatId(), getMessageId(), text);
            var parseMode = detectParseMode(text);

            if (parseMode.isPresent())
                message = message.parseMode(parseMode.get());

            if (keyboard != null) {
                message = message.replyMarkup(keyboard);
                keyboardVerbose = getInlineKeyboardVerboseString(keyboard);
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