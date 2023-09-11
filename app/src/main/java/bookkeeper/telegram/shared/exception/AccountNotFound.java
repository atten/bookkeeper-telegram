package bookkeeper.telegram.shared.exception;

public class AccountNotFound extends SkipHandlerException {
    public AccountNotFound(long id) {
        super(String.format("Account with id=%s not found", id));
    }
}
