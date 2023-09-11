package bookkeeper.service.matcher;

import bookkeeper.entity.Account;
import bookkeeper.entity.TelegramUser;
import bookkeeper.service.parser.Spending;

import java.util.Optional;

public interface AccountMatcher {

    Optional<Account> match(Spending spending, TelegramUser user);
}
