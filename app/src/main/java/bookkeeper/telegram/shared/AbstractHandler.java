package bookkeeper.telegram.shared;

import bookkeeper.entities.TelegramUser;
import bookkeeper.services.repositories.TelegramUserRepository;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractHandler {
    private final TelegramBot bot;
    private final TelegramUserRepository telegramUserRepository;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public AbstractHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository) {
        this.bot = bot;
        this.telegramUserRepository = telegramUserRepository;
    }

    public abstract Boolean handle(Update update);

    protected void sendMessage(Update update, String text, Keyboard keyboard) {
        sendMessage(update, text, Optional.of(keyboard), false);
    }

    protected void sendMessage(Update update, String text) {
        sendMessage(update, text, Optional.empty(), false);
    }

    protected void replyMessage(Update update, String text, Keyboard keyboard) {
        sendMessage(update, text, Optional.of(keyboard), true);
    }

    protected void editMessage(Update update, String text, InlineKeyboardMarkup keyboard) {
        editMessage(update, Optional.of(text), Optional.of(keyboard));
    }

    protected void editMessage(Update update, String text) {
        editMessage(update, Optional.of(text), Optional.empty());
    }

    protected void editMessage(Update update, InlineKeyboardMarkup keyboard) {
        editMessage(update, Optional.empty(), Optional.of(keyboard));
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

    private void sendMessage(Update update, String text, Optional<Keyboard> keyboard, Boolean reply) {
        var telegramUser = getTelegramUser(update);
        var parseMode = detectParseMode(text);
        var keyboardVerbose = "";

        var message = new SendMessage(telegramUser.getTelegramId(), text);

        if (parseMode.isPresent())
            message = message.parseMode(parseMode.get());

        if (keyboard.isPresent()) {
            message = message.replyMarkup(keyboard.get());

            if (keyboard.get() instanceof InlineKeyboardMarkup)
                keyboardVerbose = getInlineKeyboardVerboseString((InlineKeyboardMarkup) keyboard.get());
        }

        if (reply)
            message = message.replyToMessageId(getMessageId(update));

        var result = bot.execute(message);
        var resultVerbose = result.description() != null ? result.description() : "OK";

        logger.info("{}{} -> {} ({})", text, keyboardVerbose, telegramUser, resultVerbose);
    }

    private void editMessage(Update update, Optional<String> text, Optional<InlineKeyboardMarkup> keyboard) {
        var telegramUser = getTelegramUser(update);
        var keyboardVerbose = "";

        BaseResponse result;
        if (text.isEmpty() && keyboard.isPresent()) {
            var message = new EditMessageReplyMarkup(getChatId(update), getMessageId(update)).replyMarkup(keyboard.get());
            result = bot.execute(message);
        }
        else if (text.isPresent()) {
            var message = new EditMessageText(getChatId(update), getMessageId(update), text.get());
            var parseMode = detectParseMode(text.get());

            if (parseMode.isPresent())
                message = message.parseMode(parseMode.get());

            if (keyboard.isPresent()) {
                message = message.replyMarkup(keyboard.get());
                keyboardVerbose = getInlineKeyboardVerboseString(keyboard.get());
            }

            result = bot.execute(message);
        } else {
            throw new RuntimeException();
        }

        var resultVerbose = result.description() != null ? result.description() : "OK";
        logger.info("{}{} -> {} ({})", text.orElse("(empty)"), keyboardVerbose, telegramUser, resultVerbose);
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
