package bookkeeper.telegram.scenarios.editTransactions;

import bookkeeper.telegram.shared.CallbackMessage;

public class EditMonthlyTransactionsCallback extends CallbackMessage {
    private final int monthOffset;

    public EditMonthlyTransactionsCallback(int monthOffset) {
        this.monthOffset = monthOffset;
    }

    int getMonthOffset() {
        return monthOffset;
    }
}
