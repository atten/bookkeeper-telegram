package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

import java.util.List;

class AbstractTransactionEditCallback extends CallbackMessage {
    @Getter
    private final long transactionId;
    @Getter
    private List<Long> pendingTransactionIds = List.of();

    AbstractTransactionEditCallback(long transactionId) {
        this.transactionId = transactionId;
    }

    AbstractTransactionEditCallback setPendingTransactionIds(List<Long> pendingTransactionIds) {
        this.pendingTransactionIds = pendingTransactionIds;
        return this;
    }
}
