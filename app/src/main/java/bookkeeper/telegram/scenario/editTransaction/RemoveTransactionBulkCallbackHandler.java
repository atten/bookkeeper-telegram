package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.dao.AccountTransactionRepository;
import bookkeeper.exception.AccountTransactionNotFound;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;

import javax.inject.Inject;

import static bookkeeper.service.telegram.StringUtils.strikeoutMessage;
import static bookkeeper.telegram.scenario.editTransaction.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user cancels transaction addition in bulk.
 */
class RemoveTransactionBulkCallbackHandler implements AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    @Inject
    RemoveTransactionBulkCallbackHandler(AccountTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handle "Cancel" click: delete given transactions.
     */
    public Boolean handle(Request request) throws AccountTransactionNotFound {
        if (!(request.getCallbackMessage().orElse(null) instanceof RemoveTransactionBulkCallback cm))
            return false;

        var transactions = transactionRepository.findByIds(cm.getTransactionIds());
        transactions.forEach(transactionRepository::remove);
        request.editMessage(strikeoutMessage(getResponseMessage(transactions)));
        return true;
    }
}
