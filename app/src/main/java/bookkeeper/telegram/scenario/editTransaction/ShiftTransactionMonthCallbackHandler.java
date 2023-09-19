package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.Request;
import bookkeeper.telegram.shared.exception.AccountTransactionNotFound;

import javax.inject.Inject;
import java.time.temporal.ChronoUnit;

import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user changes transaction month.
 */
class ShiftTransactionMonthCallbackHandler implements AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    @Inject
    ShiftTransactionMonthCallbackHandler(AccountTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handle "Shift transaction month" click: subtract 1 month from current transaction timestamp.
     */
    public Boolean handle(Request request) throws AccountTransactionNotFound {
        var callbackMessage = request.getCallbackMessage();
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof ShiftTransactionMonthCallback cm))
            return false;

        var transaction = transactionRepository.get(cm.getTransactionId()).orElseThrow(() -> new AccountTransactionNotFound(cm.getTransactionId()));
        var pendingTransactionsCount = cm.getPendingTransactionIds().size();
        var days = 30 * cm.getMonthOffset();
        transaction.setTimestamp(transaction.getTimestamp().plus(days, ChronoUnit.DAYS));

        if (pendingTransactionsCount == 0) {
            request.editMessage(getResponseMessage(transaction), getResponseKeyboard(transaction));
        }
        else {
            request.editMessage(getResponseMessage(transaction, pendingTransactionsCount), getResponseKeyboard(transaction, cm.getPendingTransactionIds()));
        }

        return true;
    }
}
