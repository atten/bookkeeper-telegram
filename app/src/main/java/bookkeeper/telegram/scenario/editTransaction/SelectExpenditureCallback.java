package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.telegram.shared.CallbackMessage;

import java.util.List;

public class SelectExpenditureCallback extends CallbackMessage {
    private final long transactionId;
    private List<Long> pendingTransactionIds = List.of();

    public SelectExpenditureCallback(long transactionId) {
        this.transactionId = transactionId;
    }

    long getTransactionId() {
        return transactionId;
    }

    List<Long> getPendingTransactionIds() {
        return pendingTransactionIds;
    }

    public SelectExpenditureCallback setPendingTransactionIds(List<Long> pendingTransactionIds) {
        this.pendingTransactionIds = pendingTransactionIds;
        return this;
    }

}
