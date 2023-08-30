package bookkeeper.telegram.scenarios.addTransactions.tinkoff.matchers;

import bookkeeper.services.matchers.TimestampMatcher;
import bookkeeper.services.parsers.Spending;
import bookkeeper.telegram.scenarios.addTransactions.tinkoff.parsers.TinkoffPurchaseSmsWithDate;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Optional;

public class TinkoffTimestampMatcher implements TimestampMatcher {
    @Override
    public Optional<Instant> match(Spending spending) {
        if (spending instanceof TinkoffPurchaseSmsWithDate) {
            var date = ((TinkoffPurchaseSmsWithDate) spending).purchaseDate;
            return Optional.of(Instant.ofEpochSecond(date.toEpochSecond(LocalTime.of(0, 0), ZoneOffset.UTC)));
        }
        return Optional.empty();
    }
}
