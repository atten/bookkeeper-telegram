package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import bookkeeper.service.parser.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Example:
 * Снятие, карта *0964. 3000 RUB. ATM 123. Доступно 343.32 RUB
 */
@Data
public class TinkoffWithdrawalSms implements Spending {
    public String cardIdentifier;  // *0964
    public BigDecimal withdrawSum;  // 3000
    public Currency withdrawCurrency;  // RUB
    public String casher;  // ATM 123
    public BigDecimal accountBalance;  // 499.28
    public Currency accountCurrency;  // RUB

    @Override
    public String getMerchant() {
        return casher;
    }
}
