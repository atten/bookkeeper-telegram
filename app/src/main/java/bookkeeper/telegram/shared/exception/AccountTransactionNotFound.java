package bookkeeper.telegram.shared.exception;

public class AccountTransactionNotFound extends SkipHandlerException {
    public AccountTransactionNotFound(long id) {
        super(String.format("AccountTransaction with id=%s not found", id));
    }
}
