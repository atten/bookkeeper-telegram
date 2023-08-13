package bookkeeper.telegram.callbacks;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TransactionApproveCallback extends CallbackMessage {
    private static final String KEYWORD = "approve_transaction";
    private long transactionId;
    private List<Long> pendingTransactionIds;

    public TransactionApproveCallback() {

    }

    public TransactionApproveCallback(long transactionId) {
        this.transactionId = transactionId;
        this.pendingTransactionIds = List.of();
    }

    @Override
    public CallbackMessage parse(String message) throws ParseException {
        var parts = message.split("/");
        if (parts.length >= 2 && Objects.equals(parts[0], KEYWORD)) {
            var result = new TransactionApproveCallback(Long.parseLong(parts[1]));
            if (parts.length == 3) {
                var pendingTransactionIds = Arrays.stream(parts[2].split(",")).map(Long::parseLong).collect(Collectors.toList());
                result.setPendingTransactionIds(pendingTransactionIds);
            }
            return result;
        }
        throw new ParseException("", 0);
    }

    public long getTransactionId() {
        return transactionId;
    }

    public String toString() {
        return KEYWORD + "/" + transactionId + "/" + pendingTransactionIds.stream().map(Object::toString).collect(Collectors.joining(","));
    }

    public List<Long> getPendingTransactionIds() {
        return pendingTransactionIds;
    }

    public TransactionApproveCallback setPendingTransactionIds(List<Long> pendingTransactionIds) {
        this.pendingTransactionIds = pendingTransactionIds;
        return this;
    }
}
