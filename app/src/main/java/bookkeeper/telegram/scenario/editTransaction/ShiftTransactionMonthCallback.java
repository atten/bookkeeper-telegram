package bookkeeper.telegram.scenario.editTransaction;

import lombok.Getter;

@Getter
class ShiftTransactionMonthCallback extends AbstractTransactionEditCallback {
    private final long monthOffset;

    ShiftTransactionMonthCallback(long transactionId, long monthOffset) {
        super(transactionId);
        this.monthOffset = monthOffset;
    }
}
