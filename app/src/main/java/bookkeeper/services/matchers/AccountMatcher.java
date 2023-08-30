package bookkeeper.services.matchers;

import bookkeeper.entities.Account;
import bookkeeper.entities.TelegramUser;
import bookkeeper.services.parsers.Spending;

import java.util.Optional;

public interface AccountMatcher {

    Optional<Account> match(Spending spending, TelegramUser user);
}
