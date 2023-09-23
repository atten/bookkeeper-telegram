package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.telegram.shared.CallbackMessage;
import lombok.Getter;

import java.util.List;

class SelectAccountCallback extends CallbackMessage {
    @Getter
    private final long transactionId;
    @Getter
    private List<Long> pendingTransactionIds = List.of();

    SelectAccountCallback(long transactionId) {
        this.transactionId = transactionId;
    }

    SelectAccountCallback setPendingTransactionIds(List<Long> pendingTransactionIds) {
        this.pendingTransactionIds = pendingTransactionIds;
        return this;
    }
}
