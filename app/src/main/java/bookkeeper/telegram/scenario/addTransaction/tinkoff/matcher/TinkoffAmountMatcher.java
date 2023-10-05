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
        if (spending instanceof TinkoffPurchaseSms obj) {
            // "1 RUB" expenses are ephemeral and should be ignored (e.g. Mos.Transport uses it to check card validity)
            if (obj.purchaseSum.equals(BigDecimal.ONE) && obj.purchaseCurrency.equals(Currency.getInstance("RUB")))
                return Optional.of(BigDecimal.ZERO);

            return Optional.of(obj.purchaseSum.negate());
        }
        if (spending instanceof TinkoffFpsPurchaseSms obj) {
            return Optional.of(obj.purchaseSum.negate());
        }
        if (spending instanceof TinkoffTransferSms obj) {
            return Optional.of(obj.transferSum.negate());
        }
        if (spending instanceof TinkoffRecurringChargeSms obj) {
            return Optional.of(obj.chargeSum.negate());
        }
        if (spending instanceof TinkoffReplenishSms obj) {
            return Optional.of(obj.replenishSum);
        }
        if (spending instanceof TinkoffDepositInterestSms obj) {
            return Optional.of(obj.getInterestSum());
        }
        if (spending instanceof TinkoffIgnoreSms) {
            return Optional.of(BigDecimal.ZERO);
        }
        return Optional.empty();
    }
}
