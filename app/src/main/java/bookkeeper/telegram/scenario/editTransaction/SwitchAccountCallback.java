package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.telegram.shared.CallbackMessage;
import lombok.Getter;

import java.util.List;

class SwitchAccountCallback extends CallbackMessage {
    @Getter
    private final long transactionId;
    @Getter
    private final long accountId;
    @Getter
    private List<Long> pendingTransactionIds = List.of();

    SwitchAccountCallback(long transactionId, long accountId) {
        this.transactionId = transactionId;
        this.accountId = accountId;
    }

    SwitchAccountCallback setPendingTransactionIds(List<Long> pendingTransactionIds) {
        this.pendingTransactionIds = pendingTransactionIds;
        return this;
    }
}
