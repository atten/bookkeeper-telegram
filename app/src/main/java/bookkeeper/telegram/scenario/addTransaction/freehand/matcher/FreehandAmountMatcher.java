package bookkeeper.telegram.scenario.addTransaction.freehand.matcher;

import bookkeeper.service.matcher.AmountMatcher;
import bookkeeper.service.parser.Spending;
import bookkeeper.telegram.scenario.addTransaction.freehand.parser.FreehandRecord;

import java.math.BigDecimal;
import java.util.Optional;

public class FreehandAmountMatcher implements AmountMatcher {
    @Override
    public Optional<BigDecimal> match(Spending spending) {
        if (spending instanceof FreehandRecord obj) {
            return Optional.of(obj.amount.negate());
        }
        return Optional.empty();
    }
}
