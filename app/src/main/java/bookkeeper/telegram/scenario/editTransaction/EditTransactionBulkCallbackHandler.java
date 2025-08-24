package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.dao.repository.AccountTransactionRepository;
import bookkeeper.exception.AccountTransactionNotFound;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;

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

        if (cm.getRemainingTransactionIds().isEmpty())
            return false;

        var transaction = transactionRepository.get(cm.getRemainingTransactionIds().getFirst()).orElseThrow(() -> new AccountTransactionNotFound(cm.getRemainingTransactionIds().getFirst()));
        var pendingTransactionIds = cm.getRemainingTransactionIds().stream().skip(1).collect(Collectors.toList());
        request.editMessage(getResponseMessage(transaction, pendingTransactionIds.size()), getResponseKeyboard(transaction, cm.getAllTransactionIds(), pendingTransactionIds));
        return true;
    }
}
