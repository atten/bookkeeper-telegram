package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

/**
 * СЧЁТ1234 16:40 Оплата 223р Комиссия 22.23р АО "П-Ц" Баланс: 360.73р
 */
@Data
public class SberPurchaseFeeSms implements Spending {
    public String accountName;  // СЧЁТ1234
    public BigDecimal purchaseSum;  // 223
    public Currency purchaseCurrency;  // RUB
    public BigDecimal feeSum;  // 22.23
    public Currency feeCurrency;  // RUB
    public String merchant;  // АО "П-Ц"
    public BigDecimal accountBalance;  // 360.73
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
