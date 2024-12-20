package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Optional;

/**
 * Example:
 * Покупка 17.07.2023. Карта *0964. 56 RUB. MOS.TRANSP. Доступно 499.28 RUB
 */
@Data
public class TinkoffPurchaseSmsWithDate implements Spending {
    public LocalDate purchaseDate;  // 17.07.2023
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
