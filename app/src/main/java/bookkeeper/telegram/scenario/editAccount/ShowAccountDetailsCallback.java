package bookkeeper.telegram.scenario.editAccount;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

class ShowAccountDetailsCallback extends CallbackMessage {
    @Getter
    private final long accountId;

    ShowAccountDetailsCallback(long accountId) {
        this.accountId = accountId;
    }
}
