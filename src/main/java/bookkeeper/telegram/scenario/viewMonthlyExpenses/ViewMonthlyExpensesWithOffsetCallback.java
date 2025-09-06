package bookkeeper.telegram.scenario.viewMonthlyExpenses;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;


@Getter
class ViewMonthlyExpensesWithOffsetCallback extends CallbackMessage {
    private final int monthOffset;

    ViewMonthlyExpensesWithOffsetCallback(int monthOffset) {
        this.monthOffset = monthOffset;
    }
}
