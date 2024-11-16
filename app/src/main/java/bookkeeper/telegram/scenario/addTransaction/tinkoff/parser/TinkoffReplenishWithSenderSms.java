package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

/**
 * Example:
 * Пополнение, счет RUB. 236 RUB. Сергей С. Доступно 713.79 RUB
 */
@Data
public class TinkoffReplenishWithSenderSms implements Spending {
    public BigDecimal replenishSum;  // 236
    public Currency replenishCurrency;  // RUB
    public String replenishSender;  // Сергей С
    public BigDecimal accountBalance;  // 713.79
    public Currency accountCurrency;  // RUB

    @Override
    public String getMerchant() {
        return replenishSender;
    }

    @Override
    public Optional<BigDecimal> getBalance() {
        return Optional.of(accountBalance);
    }
}
