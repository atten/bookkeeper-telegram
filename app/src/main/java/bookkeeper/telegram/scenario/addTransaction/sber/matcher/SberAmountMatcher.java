package bookkeeper.telegram.scenario.addTransaction.sber.matcher;

import bookkeeper.service.matcher.AmountMatcher;
import bookkeeper.service.parser.Spending;
import bookkeeper.telegram.scenario.addTransaction.sber.parser.SberFpsPurchaseSms;
import bookkeeper.telegram.scenario.addTransaction.sber.parser.SberReplenishSms;

import java.math.BigDecimal;
import java.util.Optional;

public class SberAmountMatcher implements AmountMatcher {
    @Override
    public Optional<BigDecimal> match(Spending spending) {
        if (spending instanceof SberFpsPurchaseSms obj) {
            return Optional.of(obj.getPurchaseSum().negate());
        }
        if (spending instanceof SberReplenishSms obj) {
            return Optional.of(obj.getReplenishSum());
        }
        return Optional.empty();
    }
}
