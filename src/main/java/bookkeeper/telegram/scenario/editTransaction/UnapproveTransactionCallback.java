package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

@Getter
class UnapproveTransactionCallback extends CallbackMessage {
    private final long transactionId;

    UnapproveTransactionCallback(long transactionId) {
        this.transactionId = transactionId;
    }

}
