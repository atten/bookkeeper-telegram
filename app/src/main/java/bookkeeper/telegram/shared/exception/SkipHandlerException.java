package bookkeeper.telegram.shared.exception;

public class SkipHandlerException extends Exception {
    SkipHandlerException(String reason) {
        super(reason);
    }
}
