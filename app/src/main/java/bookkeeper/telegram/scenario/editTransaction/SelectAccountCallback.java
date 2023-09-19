package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.telegram.shared.CallbackMessage;
import lombok.Getter;

public class SelectAccountCallback extends CallbackMessage {
    @Getter
    private final long transactionId;

    public SelectAccountCallback(long transactionId) {
        this.transactionId = transactionId;
    }
}
