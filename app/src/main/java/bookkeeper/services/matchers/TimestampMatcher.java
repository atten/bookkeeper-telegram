package bookkeeper.services.matchers;

import bookkeeper.services.parsers.Spending;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

public interface TimestampMatcher {
    @Nullable
    Instant match(Spending spending);
}
