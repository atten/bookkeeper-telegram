package bookkeeper.telegram.scenarios.edit;

import bookkeeper.telegram.shared.CallbackMessage;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;

public class TransactionApproveBulkCallback extends CallbackMessage {
    private static final String KEYWORD = "approve_transactions";
    private List<Long> transactionIds;

    public TransactionApproveBulkCallback() {}

    public TransactionApproveBulkCallback(List<Long> transactionIds) {
        this.transactionIds = transactionIds;
    }

    @Override
    public CallbackMessage parse(String message) throws ParseException {
        var parts = message.split("/");
        if (parts.length == 2 && Objects.equals(parts[0], KEYWORD)) {
            return new TransactionApproveBulkCallback(parseIds(parts[1]));
        }
        throw new ParseException("", 0);
    }

    public List<Long> getTransactionIds() {
        return transactionIds;
    }

    public String toString() {
        return KEYWORD + "/" + idsToString(transactionIds);
    }
}
