package bookkeeper.telegram.shared.exceptions;

public class SkipHandlerException extends Exception {
    SkipHandlerException(String reason) {
        super(reason);
    }
}
