package bookkeeper.services.matchers;

import bookkeeper.services.parsers.Spending;

import java.math.BigDecimal;
import java.util.Optional;

public interface AmountMatcher {

    Optional<BigDecimal> match(Spending spending);
}
