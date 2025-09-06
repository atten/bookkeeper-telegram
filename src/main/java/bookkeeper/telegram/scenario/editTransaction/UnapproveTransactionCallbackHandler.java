package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.dao.repository.AccountTransactionRepository;
import bookkeeper.exception.AccountTransactionNotFound;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;

import javax.inject.Inject;

import static bookkeeper.telegram.scenario.editTransaction.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.scenario.editTransaction.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user revokes transaction approve.
 */
class UnapproveTransactionCallbackHandler implements AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    @Inject
    UnapproveTransactionCallbackHandler(AccountTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handle "Unapprove" click: unlock amendments for given transaction.
     */
    public Boolean handle(Request request) throws AccountTransactionNotFound {
        if (!(request.getCallbackMessage().orElse(null) instanceof UnapproveTransactionCallback cm))
            return false;

        var transaction = transactionRepository.get(cm.getTransactionId()).orElseThrow(() -> new AccountTransactionNotFound(cm.getTransactionId()));
        transactionRepository.unapprove(transaction);
        request.editMessage(getResponseMessage(transaction), getResponseKeyboard(transaction));
        return true;
    }
}
