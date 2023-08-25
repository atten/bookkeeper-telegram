package bookkeeper.telegram.scenarios.edit;

import bookkeeper.telegram.shared.CallbackMessage;

import java.util.List;

public class TransactionApproveCallback extends CallbackMessage {
    private final long transactionId;
    private List<Long> pendingTransactionIds;

    public TransactionApproveCallback(long transactionId) {
        this.transactionId = transactionId;
        this.pendingTransactionIds = List.of();
    }

    long getTransactionId() {
        return transactionId;
    }

    List<Long> getPendingTransactionIds() {
        return pendingTransactionIds;
    }

    public TransactionApproveCallback setPendingTransactionIds(List<Long> pendingTransactionIds) {
        this.pendingTransactionIds = pendingTransactionIds;
        return this;
    }
}
