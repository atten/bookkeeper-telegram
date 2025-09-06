package bookkeeper.telegram.scenario.addTransfer;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Currency;

@Getter
class AddTransferCallback extends CallbackMessage {
    private final BigDecimal withdrawAmount;
    private final BigDecimal depositAmount;
    private final Currency withdrawCurrency;
    private final Currency depositCurrency;
    private long withdrawAccountId = 0;
    private long depositAccountId = 0;
    private int monthOffset = 0;
    private boolean ready = false;

    AddTransferCallback(BigDecimal withdrawAmount, Currency withdrawCurrency, BigDecimal depositAmount, Currency depositCurrency) {
        this.withdrawAmount = withdrawAmount;
        this.depositAmount = depositAmount;
        this.withdrawCurrency = withdrawCurrency;
        this.depositCurrency = depositCurrency;
    }

    AddTransferCallback setWithdrawAccountId(long withdrawAccountId) {
        this.withdrawAccountId = withdrawAccountId;
        return this;
    }

    AddTransferCallback setDepositAccountId(long depositAccountId) {
        this.depositAccountId = depositAccountId;
        return this;
    }

    AddTransferCallback setMonthOffset(int monthOffset) {
        this.monthOffset = monthOffset;
        return this;
    }

    AddTransferCallback markReady() {
        this.ready = true;
        return this;
    }

}
