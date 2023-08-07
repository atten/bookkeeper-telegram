package bookkeeper.telegram.callbacks;

import bookkeeper.enums.Expenditure;

import java.text.ParseException;
import java.util.Objects;

public class ExpenditureAssignCallback extends CallbackMessage {
    private static final String KEYWORD = "assign_expenditure";
    private long transactionId;
    private Expenditure expenditure;

    public ExpenditureAssignCallback() {

    }

    public ExpenditureAssignCallback(long transactionId, Expenditure expenditure) {
        this.transactionId = transactionId;
        this.expenditure = expenditure;
    }

    @Override
    public CallbackMessage parse(String message) throws ParseException {
        var parts = message.split("/");
        if (parts.length == 3 && Objects.equals(parts[0], KEYWORD)) {
            return new ExpenditureAssignCallback(Long.parseLong(parts[1]), Expenditure.valueOf(parts[2]));
        }
        throw new ParseException("", 0);
    }

    public long getTransactionId() {
        return transactionId;
    }

    public Expenditure getExpenditure() {
        return expenditure;
    }

    public String toString() {
        return String.format("%s/%s/%s", KEYWORD, transactionId, expenditure);
    }
}
