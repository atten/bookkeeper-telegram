package bookkeeper.telegram.callbacks;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TransactionEditBulkCallback extends CallbackMessage {
    private static final String KEYWORD = "edit_transactions";
    private List<Long> transactionIds;

    public TransactionEditBulkCallback() {

    }

    public TransactionEditBulkCallback(List<Long> transactionIds) {
        this.transactionIds = transactionIds;
    }

    @Override
    public CallbackMessage parse(String message) throws ParseException {
        var parts = message.split("/");
        if (parts.length == 2 && Objects.equals(parts[0], KEYWORD)) {
            return new TransactionEditBulkCallback(Arrays.stream(parts[1].split(",")).map(Long::parseLong).collect(Collectors.toList()));
        }
        throw new ParseException("", 0);
    }

    public List<Long> getTransactionIds() {
        return transactionIds;
    }

    public String toString() {
        return KEYWORD + "/" + transactionIds.stream().map(Object::toString).collect(Collectors.joining(","));
    }
}
