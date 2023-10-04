package bookkeeper.telegram.scenario.viewMonthlyExpenses;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

class SelectMonthlyExpendituresCallback extends CallbackMessage {
    @Getter
    private final int monthOffset;

    SelectMonthlyExpendituresCallback(int monthOffset) {
        this.monthOffset = monthOffset;
    }
}
