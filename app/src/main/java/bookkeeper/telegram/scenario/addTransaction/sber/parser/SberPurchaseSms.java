package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

/**
 * MIR-1234 16:00 Покупка 198р PEKARNYA Баланс: 1 681.81р
 */
@Data
public class SberPurchaseSms implements Spending {
    public String accountName;  // MIR-1234
    public BigDecimal purchaseSum;  // 733.52
    public Currency purchaseCurrency;  // RUB
    public String merchant;  // Прием платежей mos
    public BigDecimal accountBalance;  // 2634.48
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
