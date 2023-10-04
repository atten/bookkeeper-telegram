package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

import java.util.List;

class ApproveTransactionBulkCallback extends CallbackMessage {
    @Getter
    private final List<Long> transactionIds;

    ApproveTransactionBulkCallback(List<Long> transactionIds) {
        this.transactionIds = transactionIds;
    }
}
