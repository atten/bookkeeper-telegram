package bookkeeper.telegram.scenarios.save.tinkoff.parsers;

import bookkeeper.services.parsers.Spending;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * Example:
 * Перевод. Счет RUB. 500 RUB. Сергей С. Баланс 653.04 RUB
 */
public class TinkoffTransferSms implements Spending {
    public BigDecimal transferSum;  // 500
    public Currency transferCurrency;  // RUB
    public String destination;  // Сергей С
    public BigDecimal accountBalance;  // 653.04
    public Currency accountCurrency;  // RUB

    @Override
    public String getMerchant() {
        return destination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TinkoffTransferSms)) return false;
        TinkoffTransferSms that = (TinkoffTransferSms) o;
        return Objects.equals(transferSum, that.transferSum) && Objects.equals(transferCurrency, that.transferCurrency) && Objects.equals(destination, that.destination) && Objects.equals(accountBalance, that.accountBalance) && Objects.equals(accountCurrency, that.accountCurrency);
    }

    @Override
    public String toString() {
        return "TinkoffTransferSms{" +
                "transferSum=" + transferSum +
                ", transferCurrency=" + transferCurrency +
                ", destination='" + destination + '\'' +
                ", accountBalance=" + accountBalance +
                ", accountCurrency=" + accountCurrency +
                '}';
    }
}
