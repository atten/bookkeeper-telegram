package bookkeeper.telegram.scenarios.edit;

import bookkeeper.telegram.shared.CallbackMessage;

import java.util.List;

public class TransactionApproveBulkCallback extends CallbackMessage {
    private final List<Long> transactionIds;

    public TransactionApproveBulkCallback(List<Long> transactionIds) {
        this.transactionIds = transactionIds;
    }

    List<Long> getTransactionIds() {
        return transactionIds;
    }

}
