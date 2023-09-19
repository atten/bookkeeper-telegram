package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.telegram.shared.CallbackMessage;
import lombok.Getter;

import java.util.List;

public class ApproveTransactionCallback extends CallbackMessage {
    @Getter
    private final long transactionId;
    @Getter
    private List<Long> pendingTransactionIds = List.of();

    public ApproveTransactionCallback(long transactionId) {
        this.transactionId = transactionId;
    }

    public ApproveTransactionCallback setPendingTransactionIds(List<Long> pendingTransactionIds) {
        this.pendingTransactionIds = pendingTransactionIds;
        return this;
    }
}
