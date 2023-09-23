package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.telegram.shared.CallbackMessage;
import lombok.Getter;

class SelectAccountCallback extends CallbackMessage {
    @Getter
    private final long transactionId;

    SelectAccountCallback(long transactionId) {
        this.transactionId = transactionId;
    }
}
