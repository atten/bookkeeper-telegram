package bookkeeper.telegram.scenarios.store.freehand.matchers;

import bookkeeper.services.matchers.AmountMatcher;
import bookkeeper.services.parsers.Spending;
import bookkeeper.telegram.scenarios.store.freehand.parsers.FreehandRecord;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public class FreehandAmountMatcher implements AmountMatcher {
    @Nullable
    @Override
    public BigDecimal match(Spending spending) {
        if (spending instanceof FreehandRecord) {
            var obj = ((FreehandRecord) spending);
            return obj.amount;
        }
        return null;
    }
}
