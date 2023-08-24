package bookkeeper.telegram.scenarios.save.freehand.matchers;

import bookkeeper.entities.TelegramUser;
import bookkeeper.enums.Expenditure;
import bookkeeper.services.matchers.ExpenditureMatcher;
import bookkeeper.services.parsers.Spending;
import bookkeeper.telegram.scenarios.save.freehand.parsers.FreehandRecord;

public class ExpenditureMatcherByDescription implements ExpenditureMatcher {

    @Override
    public Expenditure match(Spending spending, TelegramUser telegramUser) {
        if (spending instanceof FreehandRecord) {
            var obj = (FreehandRecord) spending;
            for (var expenditure : Expenditure.enabledValues()) {
                if (expenditure.getVerboseName().toLowerCase().contains(obj.description.toLowerCase()))
                    return expenditure;
            }
        }
        return Expenditure.OTHER;
    }
}
