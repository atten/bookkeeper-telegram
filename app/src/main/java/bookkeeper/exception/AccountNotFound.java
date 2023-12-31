package bookkeeper.exception;

public class AccountNotFound extends HandlerInterruptException {
    public AccountNotFound(long id) {
        super(String.format("Account with id=%s not found", id));
    }
}
