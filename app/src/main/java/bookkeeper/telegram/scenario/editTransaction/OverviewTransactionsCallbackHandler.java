package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.dao.repository.AccountTransactionRepository;
import bookkeeper.exception.AccountTransactionNotFound;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;

import javax.inject.Inject;

import static bookkeeper.telegram.scenario.editTransaction.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.scenario.editTransaction.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user clicks "Done" in transaction.
 */
class OverviewTransactionsCallbackHandler implements AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    @Inject
    OverviewTransactionsCallbackHandler(AccountTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handle "Done" click: show summary message for added/edited transactions within batch.
     */
    public Boolean handle(Request request) throws AccountTransactionNotFound {
        if (!(request.getCallbackMessage().orElse(null) instanceof OverviewTransactionsCallback cm))
            return false;

        var allAddedTransactions = transactionRepository.findByIds(cm.getAllTransactionIds());
        request.editMessage(getResponseMessage(allAddedTransactions), getResponseKeyboard(allAddedTransactions));
        return true;
    }
}
