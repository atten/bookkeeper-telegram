package bookkeeper.telegram.scenario.addTransaction.sber.matcher;

import bookkeeper.service.matcher.AmountMatcher;
import bookkeeper.service.parser.Spending;
import bookkeeper.telegram.scenario.addTransaction.sber.parser.*;

import java.math.BigDecimal;
import java.util.Optional;

public class SberAmountMatcher implements AmountMatcher {
    @Override
    public Optional<BigDecimal> match(Spending spending) {
        if (spending instanceof SberPurchaseSms obj) {
            return Optional.of(obj.getPurchaseSum().negate());
        }
        if (spending instanceof SberFpsPurchaseSms obj) {
            return Optional.of(obj.getPurchaseSum().negate());
        }
        if (spending instanceof SberRecurringChargeSms obj) {
            return Optional.of(obj.getChargeSum().negate());
        }
        if (spending instanceof SberReplenishSms obj) {
            return Optional.of(obj.getReplenishSum());
        }
        if (spending instanceof SberReplenishSmsWithSender obj) {
            return Optional.of(obj.getReplenishSum());
        }
        if (spending instanceof SberTransferSms obj) {
            return Optional.of(obj.getTransferSum().negate());
        }
        return Optional.empty();
    }
}
