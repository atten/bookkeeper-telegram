package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

/**
 * Example:
 * Выплата процентов по вкладу: 1 872.95 RUB
 */
@Data
public class TinkoffDepositInterestSms implements Spending {
    public BigDecimal interestSum;  // 1872.95
    public Currency interestCurrency;  // RUB

    @Override
    public String getMerchant() {
        return "Проценты по вкладу";
    }

    @Override
    public Optional<BigDecimal> getBalance() {
        return Optional.empty();
    }
}
