package bookkeeper.telegram.scenarios.addTransfer;

import bookkeeper.telegram.shared.CallbackMessage;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Currency;

class AddTransferCallback extends CallbackMessage {
    @Getter
    private final BigDecimal withdrawAmount;
    @Getter
    private final BigDecimal depositAmount;
    @Getter
    private final Currency withdrawCurrency;
    @Getter
    private final Currency depositCurrency;
    @Getter
    private long withdrawAccountId = 0;
    @Getter
    private long depositAccountId = 0;
    @Getter
    private int monthOffset = 0;
    @Getter
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
