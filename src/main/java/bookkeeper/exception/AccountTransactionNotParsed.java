package bookkeeper.exception;

public class AccountTransactionNotParsed extends HandlerInterruptException {
    public AccountTransactionNotParsed(String reason) {
        super(reason);
    }
}
