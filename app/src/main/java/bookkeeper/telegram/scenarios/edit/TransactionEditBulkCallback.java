package bookkeeper.telegram.scenarios.edit;

import bookkeeper.telegram.shared.CallbackMessage;

import java.util.List;

public class TransactionEditBulkCallback extends CallbackMessage {
    private final List<Long> transactionIds;

    public TransactionEditBulkCallback(List<Long> transactionIds) {
        this.transactionIds = transactionIds;
    }

    List<Long> getTransactionIds() {
        return transactionIds;
    }
}
