package bookkeeper.telegram.scenario.viewMonthlyExpenses;

import bookkeeper.dao.repository.AccountRepository;
import bookkeeper.dao.repository.AccountTransactionRepository;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;

import javax.inject.Inject;
import java.util.Objects;


/**
 * Scenario: user requests monthly expense statistics.
 */
class ViewMonthlyExpensesHandler implements AbstractHandler {
    private final MonthlyExpensesResponseFactory monthlyExpensesResponseFactory;

    @Inject
    ViewMonthlyExpensesHandler(AccountRepository accountRepository, AccountTransactionRepository transactionRepository) {
        this.monthlyExpensesResponseFactory = new MonthlyExpensesResponseFactory(accountRepository, transactionRepository);
    }

    /**
     * Display monthly expense statistics
     */
    public Boolean handle(Request request) {
        return handleCallbackMessage(request) || handleSlashExpenses(request);
    }

    private Boolean handleCallbackMessage(Request request) {
        if (!(request.getCallbackMessage().orElse(null) instanceof ViewMonthlyExpensesWithOffsetCallback cm))
            return false;

        var user = request.getTelegramUser();
        var message = monthlyExpensesResponseFactory.getMonthlyExpenses(user, cm.getMonthOffset());
        var keyboard = MonthlyExpensesResponseFactory.getMonthlyExpensesKeyboard(cm.getMonthOffset());
        request.editMessage(message, keyboard);
        return true;
    }

    private Boolean handleSlashExpenses(Request request) {
        if (!Objects.equals(request.getMessageText(), "/expenses"))
            return false;

        var user = request.getTelegramUser();
        var message = monthlyExpensesResponseFactory.getMonthlyExpenses(user, 0);
        var keyboard = MonthlyExpensesResponseFactory.getMonthlyExpensesKeyboard(0);
        request.sendMessage(message, keyboard);
        return true;
    }
}
