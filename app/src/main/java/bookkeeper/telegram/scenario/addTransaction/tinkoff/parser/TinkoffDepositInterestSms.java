package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Example:
 * Выплата процентов по вкладу: 1 872.95 RUB
 */
@Data
public class TinkoffDepositInterestSms implements Spending {
    private BigDecimal interestSum;  // 1872.95
    private Currency interestCurrency;  // RUB

    @Override
    public String getMerchant() {
        return "Проценты по вкладу";
    }
}
