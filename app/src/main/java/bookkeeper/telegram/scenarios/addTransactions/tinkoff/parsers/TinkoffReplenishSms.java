package bookkeeper.telegram.scenarios.addTransactions.tinkoff.parsers;

import bookkeeper.services.parsers.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Example:
 * Пополнение, счет RUB. 236 RUB. Доступно 713.79 RUB
 */
@Data
public class TinkoffReplenishSms implements Spending {
    public BigDecimal replenishSum;  // 236
    public Currency replenishCurrency;  // RUB
    public BigDecimal accountBalance;  // 499.28
    public Currency accountCurrency;  // RUB

    @Override
    public String getMerchant() {
        return "Пополнение";
    }
}
