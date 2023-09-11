package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.telegram.shared.CallbackMessage;

import java.util.List;

public class ShiftTransactionMonthCallback extends CallbackMessage {
    private final long transactionId;
    private final long monthOffset;
    private List<Long> pendingTransactionIds = List.of();

    public ShiftTransactionMonthCallback(long transactionId, int monthOffset) {
        this.transactionId = transactionId;
        this.monthOffset = monthOffset;
    }

    long getTransactionId() {
        return transactionId;
    }

    long getMonthOffset() {
        return monthOffset;
    }

    List<Long> getPendingTransactionIds() {
        return pendingTransactionIds;
    }

    public ShiftTransactionMonthCallback setPendingTransactionIds(List<Long> pendingTransactionIds) {
        this.pendingTransactionIds = pendingTransactionIds;
        return this;
    }
}
