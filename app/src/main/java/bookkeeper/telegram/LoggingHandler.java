package bookkeeper.telegram;

import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

/**
 * Print incoming request to console.
 */
public class LoggingHandler extends AbstractHandler {
    LoggingHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository) {
        super(bot, telegramUserRepository);
    }

    @Override
    public Boolean handle(Update update) {
        logger.info("{} -> {}", getTelegramUser(update), updateToString(update));
        return false;
    }

    private String updateToString(Update update) {
        if (update.message() != null)
            return update.message().text();
        if (update.callbackQuery() != null)
            return String.format("(callback): %s", update.callbackQuery().data());
        return String.format("(edited): %s", update.editedMessage().text());
    }
}
