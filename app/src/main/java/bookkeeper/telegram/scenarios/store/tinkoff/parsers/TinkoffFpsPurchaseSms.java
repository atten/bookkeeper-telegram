package bookkeeper.telegram.scenarios.store.tinkoff.parsers;

import bookkeeper.services.parsers.Spending;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * Оплата СБП, счет RUB. 1760 RUB. YANDEX.AFISHA. Доступно 694.79 RUB
 */
public class TinkoffFpsPurchaseSms implements Spending {
    public BigDecimal purchaseSum;  // 1760
    public Currency purchaseCurrency;  // RUB
    public String merchant;  // YANDEX.AFISHA
    public BigDecimal accountBalance;  // 694.79
    public Currency accountCurrency;  // RUB

    @NotNull
    @Override
    public String getMerchant() {
        return merchant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TinkoffFpsPurchaseSms that = (TinkoffFpsPurchaseSms) o;

        if (!Objects.equals(purchaseSum, that.purchaseSum)) return false;
        if (!Objects.equals(purchaseCurrency, that.purchaseCurrency)) return false;
        if (!Objects.equals(merchant, that.merchant)) return false;
        if (!Objects.equals(accountBalance, that.accountBalance)) return false;
        return Objects.equals(accountCurrency, that.accountCurrency);
    }

    @Override
    public String toString() {
        return "TinkoffPurchaseSms{" +
                "purchaseSum=" + purchaseSum +
                ", purchaseCurrency=" + purchaseCurrency +
                ", merchantTag='" + merchant + '\'' +
                ", accountBalance=" + accountBalance +
                ", accountCurrency=" + accountCurrency +
                '}';
    }
}
