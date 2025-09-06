package bookkeeper.exception;

public class HandlerInterruptException extends Exception {
    HandlerInterruptException(String reason) {
        super(reason);
    }
}
