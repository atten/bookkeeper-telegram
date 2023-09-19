package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.Request;
import bookkeeper.telegram.shared.exception.AccountTransactionNotFound;

import javax.inject.Inject;
import java.util.stream.Collectors;

import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user approves transaction.
 */
class ApproveTransactionCallbackHandler implements AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    @Inject
    ApproveTransactionCallbackHandler(AccountTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handle "Approve transaction" click: mark given transaction as approved.
     */
    public Boolean handle(Request request) throws AccountTransactionNotFound {
        var callbackMessage = request.getCallbackMessage();
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof ApproveTransactionCallback cm))
            return false;

        var transaction = transactionRepository.get(cm.getTransactionId()).orElseThrow(() -> new AccountTransactionNotFound(cm.getTransactionId()));
        var pendingTransactionsCount = cm.getPendingTransactionIds().size();

        transactionRepository.approve(transaction);

        if (pendingTransactionsCount == 0) {
            // show summary message for added/edited transactions within batch
            var allAddedTransactions = transactionRepository.findByCreatedAt(transaction.getCreatedAt(), request.getTelegramUser());
            request.editMessage(getResponseMessage(allAddedTransactions), getResponseKeyboard(allAddedTransactions));
        }
        else {
            var nextPendingTransactionId = cm.getPendingTransactionIds().get(0);
            var nextPendingTransaction = transactionRepository.get(nextPendingTransactionId).orElseThrow(() -> new AccountTransactionNotFound(nextPendingTransactionId));
            var remainingTransactionIds = cm.getPendingTransactionIds().stream().skip(1).collect(Collectors.toList());
            request.editMessage(getResponseMessage(nextPendingTransaction, remainingTransactionIds.size()), getResponseKeyboard(nextPendingTransaction, remainingTransactionIds));
        }

        return true;
    }
}
