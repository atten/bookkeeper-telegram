package bookkeeper.telegram.scenarios.viewMonthlyExpenses;

import bookkeeper.telegram.shared.CallbackMessage;
import lombok.Getter;

class SelectMonthlyExpendituresCallback extends CallbackMessage {
    @Getter
    private final int monthOffset;

    SelectMonthlyExpendituresCallback(int monthOffset) {
        this.monthOffset = monthOffset;
    }
}
