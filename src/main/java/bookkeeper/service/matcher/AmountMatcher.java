package bookkeeper.service.matcher;

import bookkeeper.service.parser.Spending;

import java.math.BigDecimal;
import java.util.Optional;

public interface AmountMatcher {

    Optional<BigDecimal> match(Spending spending);
}
