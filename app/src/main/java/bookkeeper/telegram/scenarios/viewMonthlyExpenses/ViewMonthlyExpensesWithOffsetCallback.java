package bookkeeper.telegram.scenarios.viewMonthlyExpenses;

import bookkeeper.telegram.shared.CallbackMessage;


public class ViewMonthlyExpensesWithOffsetCallback extends CallbackMessage {
    private final int monthOffset;

    public ViewMonthlyExpensesWithOffsetCallback(int monthOffset) {
        this.monthOffset = monthOffset;
    }

    int getMonthOffset() {
        return monthOffset;
    }
}
