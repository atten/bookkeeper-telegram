package bookkeeper.telegram.scenarios.addTransactions.tinkoff.parsers;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * Example:
 * Покупка 17.07.2023. Карта *0964. 56 RUB. MOS.TRANSP. Доступно 499.28 RUB
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TinkoffPurchaseSmsWithDate extends TinkoffPurchaseSms {
    public LocalDate purchaseDate;  // 17.07.2023
}
