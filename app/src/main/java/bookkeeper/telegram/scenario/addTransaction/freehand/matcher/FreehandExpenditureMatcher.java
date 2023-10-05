package bookkeeper.telegram.scenario.addTransaction.freehand.matcher;

import bookkeeper.entity.TelegramUser;
import bookkeeper.enums.Expenditure;
import bookkeeper.service.matcher.ExpenditureMatcher;
import bookkeeper.service.parser.Spending;
import bookkeeper.telegram.scenario.addTransaction.freehand.parser.FreehandRecord;

public class FreehandExpenditureMatcher implements ExpenditureMatcher {

    @Override
    public Expenditure match(Spending spending, TelegramUser telegramUser) {
        if (spending instanceof FreehandRecord obj) {
            for (var expenditure : Expenditure.enabledValues()) {
                if (expenditure.getVerboseName().toLowerCase().contains(obj.getDescription().toLowerCase()))
                    return expenditure;
            }
        }
        return Expenditure.OTHER;
    }
}
