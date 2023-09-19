package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.telegram.shared.CallbackMessage;
import lombok.Getter;

import java.util.List;

public class ApproveTransactionBulkCallback extends CallbackMessage {
    @Getter
    private final List<Long> transactionIds;

    public ApproveTransactionBulkCallback(List<Long> transactionIds) {
        this.transactionIds = transactionIds;
    }
}
