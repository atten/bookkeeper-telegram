package bookkeeper.telegram.scenario.editAccount;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

@Getter
class ShowAccountDetailsCallback extends CallbackMessage {
    private final long accountId;

    ShowAccountDetailsCallback(long accountId) {
        this.accountId = accountId;
    }
}
