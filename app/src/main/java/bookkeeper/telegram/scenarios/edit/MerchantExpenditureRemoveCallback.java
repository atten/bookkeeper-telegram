package bookkeeper.telegram.scenarios.edit;

import bookkeeper.enums.Expenditure;
import bookkeeper.telegram.shared.CallbackMessage;


class MerchantExpenditureRemoveCallback extends CallbackMessage {
    private final String merchant;
    private final Expenditure expenditure;

    MerchantExpenditureRemoveCallback(String merchant, Expenditure expenditure) {
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
