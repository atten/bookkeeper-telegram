package bookkeeper.telegram.scenario.addTransaction.tinkoff.parser;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Example:
 * Пополнение, счет RUB. 236 RUB. Сергей С. Доступно 713.79 RUB
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TinkoffReplenishWithSenderSms extends TinkoffReplenishSms {
    public String replenishSender;  // Сергей С

    @Override
    public String getMerchant() {
        return replenishSender;
    }
}
