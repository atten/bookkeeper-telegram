package bookkeeper.service.matcher;

import bookkeeper.dao.entity.TelegramUser;
import bookkeeper.enums.Expenditure;
import bookkeeper.service.parser.Spending;

public interface ExpenditureMatcher {

    Expenditure match(Spending spending, TelegramUser telegramUser);
}
