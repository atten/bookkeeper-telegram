package bookkeeper.telegram.scenarios.viewMonthlyExpenses;

import bookkeeper.telegram.shared.CallbackMessage;
import lombok.Getter;


public class ViewMonthlyExpensesWithOffsetCallback extends CallbackMessage {
    @Getter
    private final int monthOffset;

    public ViewMonthlyExpensesWithOffsetCallback(int monthOffset) {
        this.monthOffset = monthOffset;
    }
}
