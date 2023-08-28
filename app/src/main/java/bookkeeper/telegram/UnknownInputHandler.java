package bookkeeper.telegram;

import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

/**
 * Inform user that input can't be processed.
 */
class UnknownInputHandler extends AbstractHandler {
    UnknownInputHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository) {
        super(bot, telegramUserRepository);
    }

    @Override
    public Boolean handle(Update update) {
        sendMessage(update, "Неверная или неподдерживаемая команда, попробуйте по-другому.");
        return true;
    }
}
