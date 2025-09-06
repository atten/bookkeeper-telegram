package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.Spending;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

/**
 * Накопит. счет Премьер *1234 Капитализация на 714,75р. Баланс: 1 714,75р. Подробнее s.sber.ru/ABCDE
 */
@Data
public class SberDepositInterestSms implements Spending {
    public String accountName;  // Накопит. счет Премьер
    public String accountId;  // *1234
    public BigDecimal interestSum;  // 714.75
    public Currency interestCurrency;  // RUB
    public BigDecimal accountBalance;  // 1714.75
    public Currency accountCurrency;  // RUB

    @Override
    public String getMerchant() {
        return "Капитализация";
    }

    @Override
    public Optional<BigDecimal> getBalance() {
        return Optional.of(accountBalance);
    }
}
