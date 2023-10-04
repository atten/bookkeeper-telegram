package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;
import bookkeeper.exception.AccountTransactionNotFound;

import javax.inject.Inject;
import java.util.stream.Collectors;

import static bookkeeper.telegram.scenario.editTransaction.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.scenario.editTransaction.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user edits transactions in bulk.
 */
class EditTransactionBulkCallbackHandler implements AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    @Inject
    EditTransactionBulkCallbackHandler(AccountTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Boolean handle(Request request) throws AccountTransactionNotFound {
        if (!(request.getCallbackMessage().orElse(null) instanceof EditTransactionBulkCallback cm))
            return false;

        var transaction = transactionRepository.get(cm.getTransactionIds().get(0)).orElseThrow(() -> new AccountTransactionNotFound(cm.getTransactionIds().get(0)));
        var pendingTransactionIds = cm.getTransactionIds().stream().skip(1).collect(Collectors.toList());
        request.editMessage(getResponseMessage(transaction, pendingTransactionIds.size()), getResponseKeyboard(transaction, pendingTransactionIds));
        return true;
    }
}
