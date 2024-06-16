package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

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

    @Override
    public Optional<BigDecimal> getBalance() {
        return Optional.of(accountBalance);
    }
}
