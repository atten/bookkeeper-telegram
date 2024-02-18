package bookkeeper.telegram.scenario.editTransaction;

import lombok.Getter;

@Getter
class SwitchAccountCallback extends AbstractTransactionEditCallback {
    private final long accountId;

    SwitchAccountCallback(long transactionId, long accountId) {
        super(transactionId);
        this.accountId = accountId;
    }
}
