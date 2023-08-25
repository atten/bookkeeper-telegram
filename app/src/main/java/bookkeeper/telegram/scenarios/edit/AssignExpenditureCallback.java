package bookkeeper.telegram.scenarios.edit;

import bookkeeper.enums.Expenditure;
import bookkeeper.telegram.shared.CallbackMessage;

import java.util.List;

public class AssignExpenditureCallback extends CallbackMessage {
    private final long transactionId;
    private final Expenditure expenditure;
    private List<Long> pendingTransactionIds;

    AssignExpenditureCallback(long transactionId, Expenditure expenditure) {
        this.transactionId = transactionId;
        this.expenditure = expenditure;
        this.pendingTransactionIds = List.of();
    }

    long getTransactionId() {
        return transactionId;
    }

    public Expenditure getExpenditure() {
        return expenditure;
    }

    List<Long> getPendingTransactionIds() {
        return pendingTransactionIds;
    }

    AssignExpenditureCallback setPendingTransactionIds(List<Long> pendingTransactionIds) {
        this.pendingTransactionIds = pendingTransactionIds;
        return this;
    }

}
