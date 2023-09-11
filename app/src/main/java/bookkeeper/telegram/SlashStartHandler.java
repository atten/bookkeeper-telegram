package bookkeeper.telegram;

import bookkeeper.service.repository.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Scenario: User starts bot usage.
 */
class SlashStartHandler extends AbstractHandler {
    @Inject
    SlashStartHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository) {
        super(bot, telegramUserRepository);
    }

    /**
     * Display welcome message.
     */
    @Override
    public Boolean handle(Update update) {
        if (!Objects.equals(getMessageText(update), "/start"))
            return false;

        sendMessage(update, "Добро пожаловать!");
        return true;
    }
}
