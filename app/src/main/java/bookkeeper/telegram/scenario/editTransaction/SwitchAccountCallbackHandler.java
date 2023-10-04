package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.repository.AccountRepository;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;
import bookkeeper.exception.AccountNotFound;
import bookkeeper.exception.AccountTransactionNotFound;
import bookkeeper.exception.HandlerInterruptException;

import javax.inject.Inject;

import static bookkeeper.telegram.scenario.editTransaction.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.scenario.editTransaction.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user changes transaction account.
 */
class SwitchAccountCallbackHandler implements AbstractHandler {
    private final AccountRepository accountRepository;
    private final AccountTransactionRepository transactionRepository;

    @Inject
    SwitchAccountCallbackHandler(AccountRepository accountRepository, AccountTransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handle "Switch Account" button click: assign new account to given transaction.
     */
    public Boolean handle(Request request) throws HandlerInterruptException {
        if (!(request.getCallbackMessage().orElse(null) instanceof SwitchAccountCallback cm))
            return false;

        var transaction = transactionRepository.get(cm.getTransactionId()).orElseThrow(() -> new AccountTransactionNotFound(cm.getTransactionId()));
        var account = accountRepository.get(cm.getAccountId()).orElseThrow(() -> new AccountNotFound(cm.getAccountId()));
        var pendingTransactionsCount = cm.getPendingTransactionIds().size();

        transaction.setAccount(account);

        if (pendingTransactionsCount == 0) {
            request.editMessage(getResponseMessage(transaction), getResponseKeyboard(transaction));
        }
        else {
            request.editMessage(getResponseMessage(transaction, pendingTransactionsCount), getResponseKeyboard(transaction, cm.getPendingTransactionIds()));
        }

        return true;
    }
}
