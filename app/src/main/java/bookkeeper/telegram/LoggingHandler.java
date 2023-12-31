package bookkeeper.telegram;

import bookkeeper.enums.HandlerPriority;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

/**
 * Print incoming request to console.
 */
@Slf4j
class LoggingHandler implements AbstractHandler {
    @Inject
    LoggingHandler() {}

    @Override
    public HandlerPriority getPriority() {
        return HandlerPriority.HIGHEST_LOGGING;
    }

    public Boolean handle(Request request) {
        log.info("{} -> {}", request.getTelegramUser(), request);
        return false;
    }
}
