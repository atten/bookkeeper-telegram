package bookkeeper.telegram.shared.exception;

public class HandlerInterruptException extends Exception {
    HandlerInterruptException(String reason) {
        super(reason);
    }
}
