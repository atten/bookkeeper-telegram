package bookkeeper.telegram.shared.exception;

public class AccountTransactionNotFound extends HandlerInterruptException {
    public AccountTransactionNotFound(long id) {
        super(String.format("AccountTransaction with id=%s not found", id));
    }
}
