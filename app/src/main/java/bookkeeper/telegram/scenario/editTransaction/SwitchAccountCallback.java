package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.telegram.shared.CallbackMessage;
import lombok.Getter;

class SwitchAccountCallback extends CallbackMessage {
    @Getter
    private final long transactionId;
    @Getter
    private final long accountId;

    SwitchAccountCallback(long transactionId, long accountId) {
        this.transactionId = transactionId;
        this.accountId = accountId;
    }

}
