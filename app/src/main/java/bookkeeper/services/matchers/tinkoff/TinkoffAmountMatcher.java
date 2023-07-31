package bookkeeper.services.matchers.tinkoff;

import bookkeeper.services.matchers.AmountMatcher;
import bookkeeper.services.parsers.Spending;
import bookkeeper.services.parsers.tinkoff.TinkoffPurchaseSms;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public class TinkoffAmountMatcher implements AmountMatcher {
    @Nullable
    @Override
    public BigDecimal match(Spending spending) {
        if (spending instanceof TinkoffPurchaseSms) {
            return ((TinkoffPurchaseSms) spending).purchaseSum;
        }
        return null;
    }
}
