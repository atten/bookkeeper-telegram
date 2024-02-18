package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

import java.util.List;

@Getter
class RemoveTransactionBulkCallback extends CallbackMessage {
    private final List<Long> transactionIds;

    RemoveTransactionBulkCallback(List<Long> transactionIds) {
        this.transactionIds = transactionIds;
    }
}
