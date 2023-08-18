package bookkeeper.telegram.scenarios.store.tinkoff.parsers;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Example:
 * Покупка 17.07.2023. Карта *0964. 56 RUB. MOS.TRANSP. Доступно 499.28 RUB
 */
public class TinkoffPurchaseSmsWithDate extends TinkoffPurchaseSms {
    public LocalDate purchaseDate;  // 17.07.2023

    @Override
    public String toString() {
        return "TinkoffPurchaseSmsWithDate{" +
                "purchaseDate=" + purchaseDate +
                ", cardIdentifier='" + cardIdentifier + '\'' +
                ", purchaseSum=" + purchaseSum +
                ", purchaseCurrency=" + purchaseCurrency +
                ", merchantTag='" + merchant + '\'' +
                ", accountBalance=" + accountBalance +
                ", accountCurrency=" + accountCurrency +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TinkoffPurchaseSmsWithDate that = (TinkoffPurchaseSmsWithDate) o;
        return Objects.equals(purchaseDate, that.purchaseDate);
    }
}
