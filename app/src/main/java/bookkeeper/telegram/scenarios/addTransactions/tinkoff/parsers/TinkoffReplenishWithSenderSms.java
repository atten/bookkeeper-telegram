package bookkeeper.telegram.scenarios.addTransactions.tinkoff.parsers;

import java.util.Objects;

/**
 * Example:
 * Пополнение, счет RUB. 236 RUB. Сергей С. Доступно 713.79 RUB
 */
public class TinkoffReplenishWithSenderSms extends TinkoffReplenishSms {
    public String replenishSender;  // Сергей С

    @Override
    public String getMerchant() {
        return replenishSender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TinkoffReplenishWithSenderSms)) return false;
        if (!super.equals(o)) return false;
        TinkoffReplenishWithSenderSms that = (TinkoffReplenishWithSenderSms) o;
        return Objects.equals(replenishSender, that.replenishSender);
    }

    @Override
    public String toString() {
        return "TinkoffReplenishWithSenderSms{" +
                "replenishSender='" + replenishSender + '\'' +
                ", replenishSum=" + replenishSum +
                ", replenishCurrency=" + replenishCurrency +
                ", accountBalance=" + accountBalance +
                ", accountCurrency=" + accountCurrency +
                '}';
    }
}
