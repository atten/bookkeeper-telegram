package bookkeeper.telegram;

import bookkeeper.entities.TelegramUser;
import bookkeeper.repositories.TelegramUserRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public abstract class AbstractHandler {
    private final TelegramBot bot;
    private final TelegramUserRepository telegramUserRepository;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    AbstractHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository) {
        this.bot = bot;
        this.telegramUserRepository = telegramUserRepository;
    }

    abstract Boolean handle(Update update);

    protected void sendMessage(Update update, String text) {
        TelegramUser telegramUser = getTelegramUser(update);
        SendMessage message = new SendMessage(telegramUser.getTelegramId(), text);
        SendResponse result = bot.execute(message);
        String resultVerbose = result.description() != null ? result.description() : "OK";

        logger.info("{} {} <- {} ({})", new Date(), telegramUser, text, resultVerbose);
    }

    protected TelegramUser getTelegramUser(Update update) {
        TelegramUser telegramUser = telegramUserRepository.getOrCreate(getUser(update));
        telegramUserRepository.updateLastAccess(telegramUser);
        return telegramUser;
    }

    private User getUser(Update update) {
        if (update.message() != null)
            return update.message().from();
        return update.callbackQuery().from();
    }
}
