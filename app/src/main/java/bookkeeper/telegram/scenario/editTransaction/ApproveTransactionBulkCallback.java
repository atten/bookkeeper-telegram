package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.telegram.shared.CallbackMessage;

import java.util.List;

public class ApproveTransactionBulkCallback extends CallbackMessage {
    private final List<Long> transactionIds;

    public ApproveTransactionBulkCallback(List<Long> transactionIds) {
        this.transactionIds = transactionIds;
    }

    List<Long> getTransactionIds() {
        return transactionIds;
    }

}
