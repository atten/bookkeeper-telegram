package bookkeeper.telegram.scenarios.store.tinkoff.parsers;

import bookkeeper.services.parsers.Spending;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * Example:
 * Покупка, карта *0964. 621.8 RUB. VKUSVILL 2. Доступно 499.28 RUB
 */
public class TinkoffPurchaseSms implements Spending {
    public String cardIdentifier;  // *0964
    public BigDecimal purchaseSum;  // 621.8
    public Currency purchaseCurrency;  // RUB
    public String merchant;  // VKUSVILL 2
    public BigDecimal accountBalance;  // 499.28
    public Currency accountCurrency;  // RUB

    @Override
    public String getMerchant() {
        return merchant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TinkoffPurchaseSms that = (TinkoffPurchaseSms) o;

        if (!Objects.equals(cardIdentifier, that.cardIdentifier)) return false;
        if (!Objects.equals(purchaseSum, that.purchaseSum)) return false;
        if (!Objects.equals(purchaseCurrency, that.purchaseCurrency)) return false;
        if (!Objects.equals(merchant, that.merchant)) return false;
        if (!Objects.equals(accountBalance, that.accountBalance)) return false;
        return Objects.equals(accountCurrency, that.accountCurrency);
    }

    @Override
    public String toString() {
        return "TinkoffPurchaseSms{" +
                "cardIdentifier='" + cardIdentifier + '\'' +
                ", purchaseSum=" + purchaseSum +
                ", purchaseCurrency=" + purchaseCurrency +
                ", merchantTag='" + merchant + '\'' +
                ", accountBalance=" + accountBalance +
                ", accountCurrency=" + accountCurrency +
                '}';
    }
}
