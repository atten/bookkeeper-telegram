package bookkeeper.telegram;

import bookkeeper.enums.HandlerPriority;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.Request;

import javax.inject.Inject;

/**
 * Inform user that input can't be processed.
 */
class UnknownInputHandler implements AbstractHandler {
    @Inject
    UnknownInputHandler() {
    }

    @Override
    public HandlerPriority getPriority() {
        return HandlerPriority.LOWEST_FINALIZE;
    }

    public Boolean handle(Request request) {
        request.sendMessage("Неверная или неподдерживаемая команда, попробуйте по-другому.");
        return true;
    }
}
