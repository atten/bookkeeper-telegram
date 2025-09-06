package bookkeeper.telegram.scenario.editAccount;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

@Getter
class RenameAccountCallback extends CallbackMessage {
    private final long accountId;

    RenameAccountCallback(long accountId) {
        this.accountId = accountId;
    }
}
