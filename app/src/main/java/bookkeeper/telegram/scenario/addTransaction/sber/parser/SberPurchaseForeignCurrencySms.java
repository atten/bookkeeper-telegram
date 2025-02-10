package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

/**
 * MIR-1234 19:25 Покупка 6.50BYN (198.90р) YANDEX GO Баланс: 1 246.81р
 */
@Data
public class SberPurchaseForeignCurrencySms implements Spending {
    public String accountName;  // MIR-1234
    public BigDecimal purchaseSum;  // 6.5
    public Currency purchaseCurrency;  // BYN
    public BigDecimal purchaseNativeSum;  // 198.9
    public Currency purchaseNativeCurrency;  // RUB
    public String merchant;  // YANDEX GO
    public BigDecimal accountBalance;  // 1246.81
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
