package bookkeeper.telegram.scenario.addTransaction.tinkoff.matcher;

import bookkeeper.service.matcher.AmountMatcher;
import bookkeeper.service.parser.Spending;
import bookkeeper.telegram.scenario.addTransaction.tinkoff.parser.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

public class TinkoffAmountMatcher implements AmountMatcher {
    @Override
    public Optional<BigDecimal> match(Spending spending) {
        if (spending instanceof TinkoffPurchaseSms) {
            var obj = ((TinkoffPurchaseSms) spending);

            // "1 RUB" expenses are ephemeral and should be ignored (e.g. Mos.Transport uses it to check card validity)
            if (obj.purchaseSum.equals(BigDecimal.ONE) && obj.purchaseCurrency.equals(Currency.getInstance("RUB")))
                return Optional.of(BigDecimal.ZERO);

            return Optional.of(obj.purchaseSum.negate());
        }
        if (spending instanceof TinkoffFpsPurchaseSms) {
            var obj = ((TinkoffFpsPurchaseSms) spending);
            return Optional.of(obj.purchaseSum.negate());
        }
        if (spending instanceof TinkoffTransferSms) {
            var obj = ((TinkoffTransferSms) spending);
            return Optional.of(obj.transferSum.negate());
        }
        if (spending instanceof TinkoffRecurringChargeSms) {
            var obj = ((TinkoffRecurringChargeSms) spending);
            return Optional.of(obj.chargeSum.negate());
        }
        if (spending instanceof TinkoffReplenishSms) {
            var obj = ((TinkoffReplenishSms) spending);
            return Optional.of(obj.replenishSum);
        }
        if (spending instanceof TinkoffIgnoreSms) {
            return Optional.of(BigDecimal.ZERO);
        }
        return Optional.empty();
    }
}
