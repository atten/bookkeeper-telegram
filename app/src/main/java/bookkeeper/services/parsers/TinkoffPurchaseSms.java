package bookkeeper.services.parsers;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * Example:
 * Покупка, карта *0964. 621.8 RUB. VKUSVILL 2. Доступно 499.28 RUB
 */
public class TinkoffPurchaseSms implements BankingMessage {
    public String cardIdentifier;  // *0964
    public BigDecimal purchaseSum;  // 621.8
    public Currency purchaseCurrency;  // RUB
    public String merchantTag;  // VKUSVILL 2
    public BigDecimal accountBalance;  // 499.28
    public Currency accountCurrency;  // RUB

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TinkoffPurchaseSms that = (TinkoffPurchaseSms) o;

        if (!Objects.equals(cardIdentifier, that.cardIdentifier)) return false;
        if (!Objects.equals(purchaseSum, that.purchaseSum)) return false;
        if (!Objects.equals(purchaseCurrency, that.purchaseCurrency)) return false;
        if (!Objects.equals(merchantTag, that.merchantTag)) return false;
        if (!Objects.equals(accountBalance, that.accountBalance)) return false;
        return Objects.equals(accountCurrency, that.accountCurrency);
    }

    @Override
    public String toString() {
        return "TinkoffPurchaseSms{" +
                "cardIdentifier='" + cardIdentifier + '\'' +
                ", purchaseSum=" + purchaseSum +
                ", purchaseCurrency=" + purchaseCurrency +
                ", merchantTag='" + merchantTag + '\'' +
                ", accountBalance=" + accountBalance +
                ", accountCurrency=" + accountCurrency +
                '}';
    }
}
