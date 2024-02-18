package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.enums.Expenditure;
import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;


@Getter
class RemoveMerchantExpenditureCallback extends CallbackMessage {
    private final String merchant;
    private final Expenditure expenditure;

    RemoveMerchantExpenditureCallback(String merchant, Expenditure expenditure) {
        this.merchant = merchant;
        this.expenditure = expenditure;
    }
}
