package bookkeeper.telegram.scenarios.editTransactions;

import bookkeeper.telegram.shared.CallbackMessage;

import java.util.List;

public class EditTransactionBulkCallback extends CallbackMessage {
    private final List<Long> transactionIds;

    public EditTransactionBulkCallback(List<Long> transactionIds) {
        this.transactionIds = transactionIds;
    }

    List<Long> getTransactionIds() {
        return transactionIds;
    }
}
