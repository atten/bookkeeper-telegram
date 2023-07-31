package bookkeeper.telegram;

import bookkeeper.repositories.TelegramUserRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

/**
 * Inform user that input can't be processed.
 */
public class UnknownInputHandler extends AbstractHandler {
    UnknownInputHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository) {
        super(bot, telegramUserRepository);
    }

    @Override
    Boolean handle(Update update) {
        sendMessage(update, "Неверная или неподдерживаемая команда, попробуйте по-другому.");
        return true;
    }
}
