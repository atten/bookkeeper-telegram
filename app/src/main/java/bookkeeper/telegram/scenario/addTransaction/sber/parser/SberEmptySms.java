package bookkeeper.telegram.scenario.addTransaction.sber.parser;

import bookkeeper.service.parser.Spending;

import java.math.BigDecimal;
import java.util.Optional;


/**
 * Example:
 * Автоперевод «ABC» со счёта *1234 клиенту Иван Пупкин Б. на 100р изменён. Следующий перевод 01.09.24.
 */
public class SberEmptySms implements Spending {

    @Override
    public String getMerchant() {
        return "Sber";
    }

    @Override
    public Optional<BigDecimal> getBalance() {
        return Optional.empty();
    }
}
