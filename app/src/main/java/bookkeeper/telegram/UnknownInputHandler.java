package bookkeeper.telegram;

import bookkeeper.service.repository.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.enums.HandlerPriority;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import javax.inject.Inject;

/**
 * Inform user that input can't be processed.
 */
class UnknownInputHandler extends AbstractHandler {
    @Inject
    UnknownInputHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository) {
        super(bot, telegramUserRepository);
    }

    @Override
    public HandlerPriority getPriority() {
        return HandlerPriority.LOWEST_FINALIZE;
    }

    @Override
    public Boolean handle(Update update) {
        sendMessage(update, "Неверная или неподдерживаемая команда, попробуйте по-другому.");
        return true;
    }
}
