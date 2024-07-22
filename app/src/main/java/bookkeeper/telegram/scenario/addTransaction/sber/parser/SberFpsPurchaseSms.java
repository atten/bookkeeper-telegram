package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

/**
 * MIR-1234 20:55 Покупка по СБП 733.52р Прием платежей mos Баланс: 2 634.48р
 */
@Data
public class SberFpsPurchaseSms implements Spending {
    private String accountName;  // MIR-1234
    private BigDecimal purchaseSum;  // 733.52
    private Currency purchaseCurrency;  // RUB
    private String merchant;  // Прием платежей mos
    private BigDecimal accountBalance;  // 2634.48
    private Currency accountCurrency;  // RUB

    @Override
    public String getMerchant() {
        return merchant;
    }

    @Override
    public Optional<BigDecimal> getBalance() {
        return Optional.of(accountBalance);
    }
}
