package bookkeeper.telegram.scenarios.edit;

import bookkeeper.telegram.shared.CallbackMessage;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;

public class SelectExpenditureCallback extends CallbackMessage {
    private static final String KEYWORD = "pick_expenditure";
    private long transactionId;
    private List<Long> pendingTransactionIds;

    public SelectExpenditureCallback() {}

    public SelectExpenditureCallback(long transactionId) {
        this.transactionId = transactionId;
        this.pendingTransactionIds = List.of();
    }

    @Override
    public CallbackMessage parse(String message) throws ParseException {
        var parts = message.split("/");
        if (parts.length >= 2 && Objects.equals(parts[0], KEYWORD)) {
            var result = new SelectExpenditureCallback(Long.parseLong(parts[1]));
            if (parts.length == 3) {
                var pendingTransactionIds = parseIds(parts[2]);
                result.setPendingTransactionIds(pendingTransactionIds);
            }
            return result;
        }
        throw new ParseException("", 0);
    }

    public long getTransactionId() {
        return transactionId;
    }

    public List<Long> getPendingTransactionIds() {
        return pendingTransactionIds;
    }

    public SelectExpenditureCallback setPendingTransactionIds(List<Long> pendingTransactionIds) {
        this.pendingTransactionIds = pendingTransactionIds;
        return this;
    }

    public String toString() {
        return KEYWORD + "/" + transactionId + "/" + idsToString(pendingTransactionIds);
    }
}
