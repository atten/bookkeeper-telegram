package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

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

    @Override
    public Optional<BigDecimal> getBalance() {
        return Optional.of(accountBalance);
    }
}
