package bookkeeper.services.matchers;

import bookkeeper.entities.TelegramUser;
import bookkeeper.enums.Expenditure;
import bookkeeper.services.parsers.Spending;

public interface ExpenditureMatcher {

    Expenditure match(Spending spending, TelegramUser telegramUser);
}
