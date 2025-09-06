package bookkeeper.telegram.scenario.editAccount;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

@Getter
class SwitchAccountVisibilityCallback extends CallbackMessage {
    private final long accountId;
    private final boolean isHidden;

    SwitchAccountVisibilityCallback(long accountId, boolean isHidden) {
        this.accountId = accountId;
        this.isHidden = isHidden;
    }
}
