package bookkeeper.services.matchers;

import bookkeeper.services.parsers.Spending;

import java.time.Instant;
import java.util.Optional;

public interface TimestampMatcher {
    Optional<Instant> match(Spending spending);
}
