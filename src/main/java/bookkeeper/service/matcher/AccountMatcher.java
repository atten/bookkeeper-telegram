package bookkeeper.service.matcher;

import bookkeeper.dao.entity.Account;
import bookkeeper.dao.entity.TelegramUser;
import bookkeeper.enums.Expenditure;
import bookkeeper.service.parser.Spending;

import java.util.Optional;

public interface AccountMatcher {

    Optional<Account> match(Spending spending, Expenditure expenditure, TelegramUser user);
}
