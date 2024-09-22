package bookkeeper.exception;

public class AccountTransactionNotFound extends HandlerInterruptException {
    public AccountTransactionNotFound(long id) {
        super("AccountTransaction with id=%s not found".formatted(id));
    }
}
