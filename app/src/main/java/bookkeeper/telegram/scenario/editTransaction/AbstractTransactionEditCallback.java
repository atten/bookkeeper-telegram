package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

import java.util.List;

@Getter
class AbstractTransactionEditCallback extends CallbackMessage {
    private final long transactionId;
    private List<Long> allTransactionIds;
    private List<Long> pendingTransactionIds = List.of();

    AbstractTransactionEditCallback(long transactionId) {
        this.transactionId = transactionId;
        this.allTransactionIds = List.of(transactionId);
    }

    AbstractTransactionEditCallback setTransactionIds(List<Long> allTransactionIds, List<Long> pendingTransactionIds) {
        this.allTransactionIds = allTransactionIds;
        this.pendingTransactionIds = pendingTransactionIds;
        return this;
    }
}
