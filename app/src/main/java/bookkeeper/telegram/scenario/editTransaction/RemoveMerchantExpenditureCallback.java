package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.enums.Expenditure;
import bookkeeper.telegram.shared.CallbackMessage;
import lombok.Getter;


class RemoveMerchantExpenditureCallback extends CallbackMessage {
    @Getter
    private final String merchant;
    @Getter
    private final Expenditure expenditure;

    RemoveMerchantExpenditureCallback(String merchant, Expenditure expenditure) {
        this.merchant = merchant;
        this.expenditure = expenditure;
    }
}
