package bookkeeper.telegram.scenario.editAccount;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

class RenameAccountCallback extends CallbackMessage {
    @Getter
    private final long accountId;

    RenameAccountCallback(long accountId) {
        this.accountId = accountId;
    }
}
