package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.Request;

import javax.inject.Inject;

import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseKeyboard;


/**
 * Scenario: user approves transactions in bulk.
 */
class ApproveTransactionBulkCallbackHandler implements AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    @Inject
    ApproveTransactionBulkCallbackHandler(AccountTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handle "Approve transactions" click: mark given transactions as approved.
     */
    public Boolean handle(Request request) {
        if (!(request.getCallbackMessage().orElse(null) instanceof ApproveTransactionBulkCallback cm))
            return false;

        var transactions = transactionRepository.findByIds(cm.getTransactionIds());

        transactions.forEach(transactionRepository::approve);

        request.editMessage(getResponseKeyboard(transactions));
        return true;
    }
}
