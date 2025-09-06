package bookkeeper.telegram.scenario.editAccount;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

@Getter
class SetAccountNotesCallback extends CallbackMessage {
    private final long accountId;

    SetAccountNotesCallback(long accountId) {
        this.accountId = accountId;
    }
}
