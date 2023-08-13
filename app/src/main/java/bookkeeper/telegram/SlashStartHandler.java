package bookkeeper.telegram;

import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.util.Objects;

/**
 * Scenario: User starts bot usage.
 */
public class SlashStartHandler extends AbstractHandler {
    public SlashStartHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository) {
        super(bot, telegramUserRepository);
    }

    /**
     * Display welcome message.
     */
    @Override
    public Boolean handle(Update update) {
        if (update.message() == null || !Objects.equals(update.message().text(), "/start"))
            return false;

        sendMessage(update, "Добро пожаловать!");
        return true;
    }
}
