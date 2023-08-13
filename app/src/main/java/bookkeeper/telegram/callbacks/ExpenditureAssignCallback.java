package bookkeeper.telegram.callbacks;

import bookkeeper.enums.Expenditure;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExpenditureAssignCallback extends CallbackMessage {
    private static final String KEYWORD = "assign_expenditure";
    private long transactionId;
    private Expenditure expenditure;
    private List<Long> pendingTransactionIds;

    public ExpenditureAssignCallback() {

    }

    public ExpenditureAssignCallback(long transactionId, Expenditure expenditure) {
        this.transactionId = transactionId;
        this.expenditure = expenditure;
        this.pendingTransactionIds = List.of();
    }

    @Override
    public CallbackMessage parse(String message) throws ParseException {
        var parts = message.split("/");
        if (parts.length >= 3 && Objects.equals(parts[0], KEYWORD)) {
            var result = new ExpenditureAssignCallback(Long.parseLong(parts[1]), Expenditure.valueOf(parts[2]));
            if (parts.length == 4) {
                var pendingTransactionIds = Arrays.stream(parts[3].split(",")).map(Long::parseLong).collect(Collectors.toList());
                result.setPendingTransactionIds(pendingTransactionIds);
            }
            return result;
        }
        throw new ParseException("", 0);
    }

    public long getTransactionId() {
        return transactionId;
    }

    public Expenditure getExpenditure() {
        return expenditure;
    }

    public List<Long> getPendingTransactionIds() {
        return pendingTransactionIds;
    }

    public ExpenditureAssignCallback setPendingTransactionIds(List<Long> pendingTransactionIds) {
        this.pendingTransactionIds = pendingTransactionIds;
        return this;
    }

    public String toString() {
        return KEYWORD + "/" + transactionId + "/" + expenditure + "/" + pendingTransactionIds.stream().map(Object::toString).collect(Collectors.joining(","));
    }
}
