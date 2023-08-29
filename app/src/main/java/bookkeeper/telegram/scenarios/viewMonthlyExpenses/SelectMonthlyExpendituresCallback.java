package bookkeeper.telegram.scenarios.viewMonthlyExpenses;

import bookkeeper.telegram.shared.CallbackMessage;

class SelectMonthlyExpendituresCallback extends CallbackMessage {
    private final int monthOffset;

    SelectMonthlyExpendituresCallback(int monthOffset) {
        this.monthOffset = monthOffset;
    }

    int getMonthOffset() {
        return monthOffset;
    }
}
