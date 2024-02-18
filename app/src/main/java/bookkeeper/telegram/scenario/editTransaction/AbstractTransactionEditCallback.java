package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

import java.util.List;

@Getter
class AbstractTransactionEditCallback extends CallbackMessage {
    private final long transactionId;
    private List<Long> pendingTransactionIds = List.of();

    AbstractTransactionEditCallback(long transactionId) {
        this.transactionId = transactionId;
    }

    AbstractTransactionEditCallback setPendingTransactionIds(List<Long> pendingTransactionIds) {
        this.pendingTransactionIds = pendingTransactionIds;
        return this;
    }
}
