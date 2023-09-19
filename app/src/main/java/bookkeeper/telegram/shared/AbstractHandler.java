package bookkeeper.telegram.shared;

import bookkeeper.enums.HandlerPriority;
import bookkeeper.telegram.shared.exception.HandlerInterruptException;

public interface AbstractHandler {

    Boolean handle(Request request) throws HandlerInterruptException;

    default HandlerPriority getPriority() {
        return HandlerPriority.NORMAL_COMMAND;
    }

}
