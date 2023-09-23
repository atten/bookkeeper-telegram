package bookkeeper.telegram.scenario.editTransaction;

import lombok.Getter;

class SwitchAccountCallback extends AbstractTransactionEditCallback {
    @Getter
    private final long accountId;

    SwitchAccountCallback(long transactionId, long accountId) {
        super(transactionId);
        this.accountId = accountId;
    }
}
