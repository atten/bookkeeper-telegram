package bookkeeper.exception;

public class AccountTransferNotFound extends HandlerInterruptException {
    public AccountTransferNotFound(long id) {
        super("AccountTransfer with id=%s not found".formatted(id));
    }
}
