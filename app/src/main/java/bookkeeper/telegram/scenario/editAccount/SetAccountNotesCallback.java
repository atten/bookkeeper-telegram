package bookkeeper.telegram.scenario.editAccount;

import bookkeeper.telegram.shared.CallbackMessage;
import lombok.Getter;

class SetAccountNotesCallback extends CallbackMessage {
    @Getter
    private final long accountId;

    SetAccountNotesCallback(long accountId) {
        this.accountId = accountId;
    }
}
