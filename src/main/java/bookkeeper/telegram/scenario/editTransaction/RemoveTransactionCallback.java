package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

@Getter
class RemoveTransactionCallback extends CallbackMessage {
    private final long transactionId;

    RemoveTransactionCallback(long transactionId) {
        this.transactionId = transactionId;
    }

}
