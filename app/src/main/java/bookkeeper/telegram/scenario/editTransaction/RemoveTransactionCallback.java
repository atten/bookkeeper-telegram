package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.telegram.shared.CallbackMessage;
import lombok.Getter;

class RemoveTransactionCallback extends CallbackMessage {
    @Getter
    private final long transactionId;

    RemoveTransactionCallback(long transactionId) {
        this.transactionId = transactionId;
    }

}
