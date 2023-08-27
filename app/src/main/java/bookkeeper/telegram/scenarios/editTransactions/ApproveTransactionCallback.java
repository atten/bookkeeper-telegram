package bookkeeper.telegram.scenarios.editTransactions;

import bookkeeper.telegram.shared.CallbackMessage;

import java.util.List;

public class ApproveTransactionCallback extends CallbackMessage {
    private final long transactionId;
    private List<Long> pendingTransactionIds = List.of();

    public ApproveTransactionCallback(long transactionId) {
        this.transactionId = transactionId;
    }

    long getTransactionId() {
        return transactionId;
    }

    List<Long> getPendingTransactionIds() {
        return pendingTransactionIds;
    }

    public ApproveTransactionCallback setPendingTransactionIds(List<Long> pendingTransactionIds) {
        this.pendingTransactionIds = pendingTransactionIds;
        return this;
    }
}
