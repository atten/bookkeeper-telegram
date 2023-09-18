package bookkeeper.telegram;

import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.Request;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Scenario: User starts bot usage.
 */
class SlashStartHandler implements AbstractHandler {
    @Inject
    SlashStartHandler() {}

    /**
     * Display welcome message.
     */
    public Boolean handle(Request request) {
        if (!Objects.equals(request.getMessageText(), "/start"))
            return false;

        request.sendMessage("Добро пожаловать!");
        return true;
    }
}
