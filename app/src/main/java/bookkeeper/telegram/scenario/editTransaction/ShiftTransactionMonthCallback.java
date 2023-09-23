package bookkeeper.telegram.scenario.editTransaction;

import lombok.Getter;

class ShiftTransactionMonthCallback extends AbstractTransactionEditCallback {
    @Getter
    private final long monthOffset;

    ShiftTransactionMonthCallback(long transactionId, long monthOffset) {
        super(transactionId);
        this.monthOffset = monthOffset;
    }
}
