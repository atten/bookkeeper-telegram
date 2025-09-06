package bookkeeper.telegram.scenario.editAccount;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

@Getter
public class ListAccountsCallback extends CallbackMessage {
    private final boolean includeHidden;

    public ListAccountsCallback(boolean includeHidden) {
        this.includeHidden = includeHidden;
    }
}
