package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.telegram.shared.CallbackMessage;
import lombok.Getter;

import java.util.List;

public class EditTransactionBulkCallback extends CallbackMessage {
    @Getter
    private final List<Long> transactionIds;

    public EditTransactionBulkCallback(List<Long> transactionIds) {
        this.transactionIds = transactionIds;
    }
}
