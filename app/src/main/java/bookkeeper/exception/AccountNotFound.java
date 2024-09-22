package bookkeeper.exception;

public class AccountNotFound extends HandlerInterruptException {
    public AccountNotFound(long id) {
        super("Account with id=%s not found".formatted(id));
    }
}
