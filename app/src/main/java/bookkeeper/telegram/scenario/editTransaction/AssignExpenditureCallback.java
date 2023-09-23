package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.enums.Expenditure;
import lombok.Getter;

class AssignExpenditureCallback extends AbstractTransactionEditCallback {
    @Getter
    private final Expenditure expenditure;

    AssignExpenditureCallback(long transactionId, Expenditure expenditure) {
        super(transactionId);
        this.expenditure = expenditure;
    }
}
