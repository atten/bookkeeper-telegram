package bookkeeper.services.matchers.tinkoff;

import bookkeeper.services.matchers.TimestampMatcher;
import bookkeeper.services.parsers.Spending;
import bookkeeper.services.parsers.tinkoff.TinkoffPurchaseSmsWithDate;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;

public class TinkoffTimestampMatcher implements TimestampMatcher {
    @Nullable
    @Override
    public Instant match(Spending spending) {
        if (spending instanceof TinkoffPurchaseSmsWithDate) {
            var date = ((TinkoffPurchaseSmsWithDate) spending).purchaseDate;
            return Instant.ofEpochSecond(date.toEpochSecond(LocalTime.of(0,0), ZoneOffset.UTC));
        }
        return null;
    }
}
