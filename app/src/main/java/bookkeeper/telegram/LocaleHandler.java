package bookkeeper.telegram;

import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.util.Locale;

/**
 * Set regional settings (e.g. language) for current request.
 */
class LocaleHandler extends AbstractHandler {
    LocaleHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository) {
        super(bot, telegramUserRepository);
    }

    @Override
    public Boolean handle(Update update) {
        var languageCode = getUser(update).languageCode();
        Locale.setDefault(Locale.forLanguageTag(languageCode));
        return false;
    }

}
