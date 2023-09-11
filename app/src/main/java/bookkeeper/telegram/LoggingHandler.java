package bookkeeper.telegram;

import bookkeeper.service.repository.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.enums.HandlerPriority;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

/**
 * Print incoming request to console.
 */
@Slf4j
class LoggingHandler extends AbstractHandler {
    @Inject
    LoggingHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository) {
        super(bot, telegramUserRepository);
    }

    @Override
    public HandlerPriority getPriority() {
        return HandlerPriority.HIGHEST_LOGGING;
    }

    @Override
    public Boolean handle(Update update) {
        log.info("{} -> {}", getTelegramUser(update), updateToString(update));
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
