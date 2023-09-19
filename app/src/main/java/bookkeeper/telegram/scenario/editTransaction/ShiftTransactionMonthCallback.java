package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.telegram.shared.CallbackMessage;
import lombok.Getter;

import java.util.List;

public class ShiftTransactionMonthCallback extends CallbackMessage {
    @Getter
    private final long transactionId;
    @Getter
    private final long monthOffset;
    @Getter
    private List<Long> pendingTransactionIds = List.of();

    public ShiftTransactionMonthCallback(long transactionId, int monthOffset) {
        this.transactionId = transactionId;
        this.monthOffset = monthOffset;
    }

    public ShiftTransactionMonthCallback setPendingTransactionIds(List<Long> pendingTransactionIds) {
        this.pendingTransactionIds = pendingTransactionIds;
        return this;
    }
}
