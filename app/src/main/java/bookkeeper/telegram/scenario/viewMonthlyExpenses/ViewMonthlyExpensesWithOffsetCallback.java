package bookkeeper.telegram.scenario.viewMonthlyExpenses;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;


public class ViewMonthlyExpensesWithOffsetCallback extends CallbackMessage {
    @Getter
    private final int monthOffset;

    public ViewMonthlyExpensesWithOffsetCallback(int monthOffset) {
        this.monthOffset = monthOffset;
    }
}
