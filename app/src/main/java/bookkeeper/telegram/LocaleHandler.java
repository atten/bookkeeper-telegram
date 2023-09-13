package bookkeeper.telegram;

import bookkeeper.service.repository.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.enums.HandlerPriority;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import javax.inject.Inject;
import java.util.Locale;

/**
 * Set regional settings (e.g. language) for current request.
 */
class LocaleHandler extends AbstractHandler {
    @Inject
    LocaleHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository) {
        super(bot, telegramUserRepository);
    }

    @Override
    public HandlerPriority getPriority() {
        return HandlerPriority.HIGH_CONFIGURATION;
    }

    @Override
    public Boolean handle(Update update) {
        var user = getTelegramUser(update);
        Locale.setDefault(Locale.forLanguageTag(user.getLanguageCode()));
        return false;
    }

}
