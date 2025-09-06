package bookkeeper.service.parser;

import java.math.BigDecimal;
import java.util.Optional;

public interface Spending {
    String getMerchant();

    Optional<BigDecimal> getBalance();
}
