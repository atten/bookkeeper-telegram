package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.enums.Expenditure;
import bookkeeper.telegram.shared.CallbackMessage;


class RemoveMerchantExpenditureCallback extends CallbackMessage {
    private final String merchant;
    private final Expenditure expenditure;

    RemoveMerchantExpenditureCallback(String merchant, Expenditure expenditure) {
        this.merchant = merchant;
        this.expenditure = expenditure;
    }

    String getMerchant() {
        return merchant;
    }

    Expenditure getExpenditure() {
        return expenditure;
    }
}
