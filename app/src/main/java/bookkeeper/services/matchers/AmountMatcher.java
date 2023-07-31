package bookkeeper.services.matchers;

import bookkeeper.services.parsers.Spending;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public interface AmountMatcher {

    @Nullable
    BigDecimal match(Spending spending);
}
