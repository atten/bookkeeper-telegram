package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.telegram.shared.CallbackMessage;
import lombok.Getter;

import java.util.List;

class ApproveTransactionCallback extends CallbackMessage {
    @Getter
    private final long transactionId;
    @Getter
    private List<Long> pendingTransactionIds = List.of();

    ApproveTransactionCallback(long transactionId) {
        this.transactionId = transactionId;
    }

    ApproveTransactionCallback setPendingTransactionIds(List<Long> pendingTransactionIds) {
        this.pendingTransactionIds = pendingTransactionIds;
        return this;
    }
}
