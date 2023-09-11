package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.telegram.shared.CallbackMessage;

public class RemoveTransactionCallback extends CallbackMessage {
    private final long transactionId;

    public RemoveTransactionCallback(long transactionId) {
        this.transactionId = transactionId;
    }

    long getTransactionId() {
        return transactionId;
    }

}
