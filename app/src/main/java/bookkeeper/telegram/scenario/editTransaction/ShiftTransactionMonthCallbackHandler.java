package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.dao.AccountTransactionRepository;
import bookkeeper.exception.AccountTransactionNotFound;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;

import javax.inject.Inject;
import java.time.ZoneId;

import static bookkeeper.telegram.scenario.editTransaction.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.scenario.editTransaction.TransactionResponseFactory.getResponseMessage;


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
        if (!(request.getCallbackMessage().orElse(null) instanceof ShiftTransactionMonthCallback cm))
            return false;

        var transaction = transactionRepository.get(cm.getTransactionId()).orElseThrow(() -> new AccountTransactionNotFound(cm.getTransactionId()));
        var pendingTransactionsCount = cm.getPendingTransactionIds().size();
        transaction.setTimestamp(transaction.date().plusMonths(cm.getMonthOffset()).atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (pendingTransactionsCount == 0) {
            request.editMessage(getResponseMessage(transaction), getResponseKeyboard(transaction));
        }
        else {
            request.editMessage(getResponseMessage(transaction, pendingTransactionsCount), getResponseKeyboard(transaction, cm.getAllTransactionIds(), cm.getPendingTransactionIds()));
        }

        return true;
    }
}
