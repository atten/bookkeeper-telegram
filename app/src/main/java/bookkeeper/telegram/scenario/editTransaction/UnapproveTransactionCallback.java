package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

class UnapproveTransactionCallback extends CallbackMessage {
    @Getter
    private final long transactionId;

    UnapproveTransactionCallback(long transactionId) {
        this.transactionId = transactionId;
    }

}
