package bookkeeper.telegram.scenarios.edit;

import bookkeeper.telegram.shared.CallbackMessage;

public class RefineMonthlyTransactionsCallback extends CallbackMessage {
    private final int monthOffset;

    public RefineMonthlyTransactionsCallback(int monthOffset) {
        this.monthOffset = monthOffset;
    }

    int getMonthOffset() {
        return monthOffset;
    }
}
