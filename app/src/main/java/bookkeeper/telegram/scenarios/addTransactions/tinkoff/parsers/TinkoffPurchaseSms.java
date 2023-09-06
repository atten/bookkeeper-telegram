package bookkeeper.telegram.scenarios.addTransactions.tinkoff.parsers;

import bookkeeper.services.parsers.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Example:
 * Покупка, карта *0964. 621.8 RUB. VKUSVILL 2. Доступно 499.28 RUB
 */
@Data
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
}
