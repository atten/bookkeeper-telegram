package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

import java.util.List;

@Getter
public class EditTransactionBulkCallback extends CallbackMessage {
    private final List<Long> allTransactionIds;
    private final List<Long> remainingTransactionIds;

    public EditTransactionBulkCallback(List<Long> allTransactionIds, List<Long> remainingTransactionIds) {
        this.allTransactionIds = allTransactionIds;
        this.remainingTransactionIds = remainingTransactionIds;
    }
}
