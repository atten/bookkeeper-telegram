package bookkeeper.service.matcher;

import bookkeeper.service.parser.Spending;

import java.time.Instant;
import java.util.Optional;

public interface TimestampMatcher {
    Optional<Instant> match(Spending spending);
}
