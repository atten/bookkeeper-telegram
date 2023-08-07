package bookkeeper.telegram.callbacks;

import java.text.ParseException;
import java.util.Objects;

public class ExpenditurePickCallback extends CallbackMessage {
    private static final String KEYWORD = "pick_expenditure";
    private long transactionId;

    public ExpenditurePickCallback() {

    }

    public ExpenditurePickCallback(long transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public CallbackMessage parse(String message) throws ParseException {
        var parts = message.split("/");
        if (parts.length == 2 && Objects.equals(parts[0], KEYWORD)) {
            return new ExpenditurePickCallback(Long.parseLong(parts[1]));
        }
        throw new ParseException("", 0);
    }

    public long getTransactionId() {
        return transactionId;
    }

    public String toString() {
        return String.format("%s/%s", KEYWORD, transactionId);
    }
}
