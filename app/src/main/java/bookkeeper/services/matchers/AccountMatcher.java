package bookkeeper.services.matchers;

import bookkeeper.entities.Account;
import bookkeeper.entities.TelegramUser;
import bookkeeper.services.parsers.Spending;
import org.jetbrains.annotations.Nullable;

public interface AccountMatcher {

    @Nullable Account match(Spending spending, TelegramUser user);
}
