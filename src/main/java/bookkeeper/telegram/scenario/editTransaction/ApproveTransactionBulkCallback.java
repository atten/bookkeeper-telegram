package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

import java.util.List;

@Getter
class ApproveTransactionBulkCallback extends CallbackMessage {
    private final List<Long> transactionIds;

    ApproveTransactionBulkCallback(List<Long> transactionIds) {
        this.transactionIds = transactionIds;
    }
}
