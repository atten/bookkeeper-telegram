package bookkeeper.telegram.scenario.addTransaction.tinkoff.matcher;

import bookkeeper.service.matcher.TimestampMatcher;
import bookkeeper.service.parser.Spending;
import bookkeeper.telegram.scenario.addTransaction.tinkoff.parser.TinkoffPurchaseSmsWithDate;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Optional;

public class TinkoffTimestampMatcher implements TimestampMatcher {
    @Override
    public Optional<Instant> match(Spending spending) {
        if (spending instanceof TinkoffPurchaseSmsWithDate obj) {
            var date = obj.purchaseDate;
            return Optional.of(Instant.ofEpochSecond(date.toEpochSecond(LocalTime.of(0, 0), ZoneOffset.UTC)));
        }
        return Optional.empty();
    }
}
