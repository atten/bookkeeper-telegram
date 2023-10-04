package bookkeeper.telegram;

import bookkeeper.enums.HandlerPriority;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;

import javax.inject.Inject;
import java.util.Locale;

/**
 * Set regional settings (e.g. language) for current request.
 */
class LocaleHandler implements AbstractHandler {
    @Inject
    LocaleHandler() {}

    @Override
    public HandlerPriority getPriority() {
        return HandlerPriority.HIGH_CONFIGURATION;
    }

    public Boolean handle(Request request) {
        var user = request.getTelegramUser();
        Locale.setDefault(Locale.forLanguageTag(user.getLanguageCode()));
        return false;
    }

}
