package bookkeeper.telegram.scenarios.addTransactions.tinkoff.parsers;

import bookkeeper.services.parsers.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Оплата СБП, счет RUB. 1760 RUB. YANDEX.AFISHA. Доступно 694.79 RUB
 */
@Data
public class TinkoffFpsPurchaseSms implements Spending {
    public BigDecimal purchaseSum;  // 1760
    public Currency purchaseCurrency;  // RUB
    public String merchant;  // YANDEX.AFISHA
    public BigDecimal accountBalance;  // 694.79
    public Currency accountCurrency;  // RUB

    @Override
    public String getMerchant() {
        return merchant;
    }
}
