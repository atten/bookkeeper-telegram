package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.Request;
import bookkeeper.telegram.shared.exception.AccountTransactionNotFound;

import javax.inject.Inject;

import static bookkeeper.telegram.shared.StringUtil.strikeoutMessage;
import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user cancels transaction addition.
 */
class RemoveTransactionCallbackHandler implements AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    @Inject
    RemoveTransactionCallbackHandler(AccountTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handle "Cancel" click: delete given transaction.
     */
    public Boolean handle(Request request) throws AccountTransactionNotFound {
        var callbackMessage = request.getCallbackMessage();
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof RemoveTransactionCallback cm))
            return false;

        var transaction = transactionRepository.get(cm.getTransactionId()).orElseThrow(() -> new AccountTransactionNotFound(cm.getTransactionId()));
        transactionRepository.remove(transaction);
        request.editMessage(strikeoutMessage(getResponseMessage(transaction)));
        return true;
    }
}
