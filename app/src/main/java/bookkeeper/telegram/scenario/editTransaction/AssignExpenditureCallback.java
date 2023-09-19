package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.enums.Expenditure;
import bookkeeper.telegram.shared.CallbackMessage;
import lombok.Getter;

import java.util.List;

class AssignExpenditureCallback extends CallbackMessage {
    @Getter
    private final long transactionId;
    @Getter
    private final Expenditure expenditure;
    @Getter
    private List<Long> pendingTransactionIds = List.of();

    AssignExpenditureCallback(long transactionId, Expenditure expenditure) {
        this.transactionId = transactionId;
        this.expenditure = expenditure;
    }

    AssignExpenditureCallback setPendingTransactionIds(List<Long> pendingTransactionIds) {
        this.pendingTransactionIds = pendingTransactionIds;
        return this;
    }

}
