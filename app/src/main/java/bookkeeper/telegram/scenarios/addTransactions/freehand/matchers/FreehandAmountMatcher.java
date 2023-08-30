package bookkeeper.telegram.scenarios.addTransactions.freehand.matchers;

import bookkeeper.services.matchers.AmountMatcher;
import bookkeeper.services.parsers.Spending;
import bookkeeper.telegram.scenarios.addTransactions.freehand.parsers.FreehandRecord;

import java.math.BigDecimal;
import java.util.Optional;

public class FreehandAmountMatcher implements AmountMatcher {
    @Override
    public Optional<BigDecimal> match(Spending spending) {
        if (spending instanceof FreehandRecord) {
            var obj = ((FreehandRecord) spending);
            return Optional.of(obj.amount.negate());
        }
        return Optional.empty();
    }
}
