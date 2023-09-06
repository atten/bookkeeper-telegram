package bookkeeper.telegram.scenarios.addTransactions.tinkoff.parsers;

import bookkeeper.services.parsers.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Example:
 * Перевод. Счет RUB. 500 RUB. Сергей С. Баланс 653.04 RUB
 */
@Data
public class TinkoffTransferSms implements Spending {
    public BigDecimal transferSum;  // 500
    public Currency transferCurrency;  // RUB
    public String destination;  // Сергей С
    public BigDecimal accountBalance;  // 653.04
    public Currency accountCurrency;  // RUB

    @Override
    public String getMerchant() {
        return destination;
    }
}
