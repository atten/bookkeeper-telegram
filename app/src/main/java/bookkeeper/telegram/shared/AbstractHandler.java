package bookkeeper.telegram.shared;

import bookkeeper.entity.TelegramUser;
import bookkeeper.enums.HandlerPriority;
import bookkeeper.service.repository.TelegramUserRepository;
import bookkeeper.telegram.shared.exception.SkipHandlerException;
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
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public abstract class AbstractHandler {
    private final TelegramBot bot;
    private final TelegramUserRepository telegramUserRepository;

    public AbstractHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository) {
        this.bot = bot;
        this.telegramUserRepository = telegramUserRepository;
    }

    public abstract Boolean handle(Update update) throws SkipHandlerException;

    public HandlerPriority getPriority() {
        return HandlerPriority.NORMAL_COMMAND;
    }

    protected void sendMessage(Update update, String text, Keyboard keyboard) {
        sendMessage(update, text, keyboard, false);
    }

    public void sendMessage(Update update, String text) {
        sendMessage(update, text, null, false);
    }

    protected void replyMessage(Update update, String text, Keyboard keyboard) {
        sendMessage(update, text, keyboard, true);
    }

    protected void editMessage(Update update, String text, InlineKeyboardMarkup keyboard) {
        editMessagePrivate(update, text, keyboard);
    }

    protected void editMessage(Update update, String text) {
        editMessage(update, text, null);
    }

    protected void editMessage(Update update, InlineKeyboardMarkup keyboard) {
        editMessagePrivate(update, null, keyboard);
    }

    protected TelegramUser getTelegramUser(Update update) {
        var telegramUser = telegramUserRepository.getOrCreate(getUser(update));
        telegramUserRepository.updateLastAccess(telegramUser);
        return telegramUser;
    }

    protected User getUser(Update update) {
        if (update.message() != null)
            return update.message().from();
        if (update.callbackQuery() != null)
            return update.callbackQuery().from();
        return update.editedMessage().from();
    }

    protected String getMessageText(Update update) {
        if (update.message() == null)
            return "";
        return cleanString(update.message().text());
    }

    private void sendMessage(Update update, String text, @Nullable Keyboard keyboard, Boolean reply) {
        var telegramUser = getTelegramUser(update);
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
            message = message.replyToMessageId(getMessageId(update));

        var result = bot.execute(message);
        var resultVerbose = result.description() != null ? result.description() : "OK";

        log.info("{}{} -> {} ({})", text, keyboardVerbose, telegramUser, resultVerbose);
    }

    private void editMessagePrivate(Update update, @Nullable String text, @Nullable InlineKeyboardMarkup keyboard) {
        var telegramUser = getTelegramUser(update);
        var keyboardVerbose = "";

        BaseResponse result;
        if (text == null && keyboard != null) {
            var message = new EditMessageReplyMarkup(getChatId(update), getMessageId(update)).replyMarkup(keyboard);
            result = bot.execute(message);
        }
        else if (text != null) {
            var message = new EditMessageText(getChatId(update), getMessageId(update), text);
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
        log.info("{}{} -> {} ({})", testVerbose, keyboardVerbose, telegramUser, resultVerbose);
    }

    private Optional<ParseMode> detectParseMode(String message) {
        if (message.contains("<") && message.contains(">"))
            return Optional.of(ParseMode.HTML);
        if (message.contains("`") || message.contains("*"))
            return Optional.of(ParseMode.Markdown);
        return Optional.empty();
    }

    /**
     * Replace non-breaking spaces with regular one.
     */
    private String cleanString(String input) {
        return input.replaceAll(Arrays.toString(Character.toChars(160)), " ");
    }

    private long getChatId(Update update) {
        if (update.message() != null)
            return update.message().chat().id();
        if (update.callbackQuery() != null)
            return update.callbackQuery().message().chat().id();
        return update.editedMessage().chat().id();
    }

    private int getMessageId(Update update) {
        if (update.message() != null)
            return update.message().messageId();
        return update.callbackQuery().message().messageId();
    }

    private String getInlineKeyboardVerboseString(InlineKeyboardMarkup keyboard) {
        var buttonsVerbose = Arrays.stream(keyboard.inlineKeyboard()).flatMap(Stream::of).map(InlineKeyboardButton::callbackData).collect(Collectors.joining(", "));
        return String.format("[%s]", buttonsVerbose);
    }
}
