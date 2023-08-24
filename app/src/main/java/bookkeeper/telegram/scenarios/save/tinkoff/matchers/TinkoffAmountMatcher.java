package bookkeeper.telegram.scenarios.save.tinkoff.matchers;

import bookkeeper.services.matchers.AmountMatcher;
import bookkeeper.services.parsers.Spending;
import bookkeeper.telegram.scenarios.save.tinkoff.parsers.*;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Currency;

public class TinkoffAmountMatcher implements AmountMatcher {
    @Nullable
    @Override
    public BigDecimal match(Spending spending) {
        if (spending instanceof TinkoffPurchaseSms) {
            var obj = ((TinkoffPurchaseSms) spending);

            // "1 RUB" expenses are ephemeral and should be ignored (e.g. Mos.Transport uses it to check card validity)
            if (obj.purchaseSum.equals(BigDecimal.ONE) && obj.purchaseCurrency.equals(Currency.getInstance("RUB")))
                return BigDecimal.ZERO;

            return obj.purchaseSum;
        }
        if (spending instanceof TinkoffFpsPurchaseSms) {
            var obj = ((TinkoffFpsPurchaseSms) spending);
            return obj.purchaseSum;
        }
        if (spending instanceof TinkoffTransferSms) {
            var obj = ((TinkoffTransferSms) spending);
            return obj.transferSum;
        }
        if (spending instanceof TinkoffRecurringChargeSms) {
            var obj = ((TinkoffRecurringChargeSms) spending);
            return obj.chargeSum;
        }
        if (spending instanceof TinkoffReplenishSms) {
            var obj = ((TinkoffReplenishSms) spending);
            return obj.replenishSum.negate();
        }
        if (spending instanceof TinkoffIgnoreSms) {
            return BigDecimal.ZERO;
        }
        return null;
    }
}
