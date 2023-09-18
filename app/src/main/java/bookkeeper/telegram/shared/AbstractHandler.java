package bookkeeper.telegram.shared;

import bookkeeper.enums.HandlerPriority;
import bookkeeper.telegram.shared.exception.SkipHandlerException;

public interface AbstractHandler {

    Boolean handle(Request request) throws SkipHandlerException;

    default HandlerPriority getPriority() {
        return HandlerPriority.NORMAL_COMMAND;
    }

}
