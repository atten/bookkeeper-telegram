package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.telegram.shared.CallbackMessage;
import lombok.Getter;

public class RemoveTransactionCallback extends CallbackMessage {
    @Getter
    private final long transactionId;

    public RemoveTransactionCallback(long transactionId) {
        this.transactionId = transactionId;
    }

}
