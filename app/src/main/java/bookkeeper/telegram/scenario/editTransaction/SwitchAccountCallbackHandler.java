package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.repository.AccountRepository;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.Request;
import bookkeeper.telegram.shared.exception.AccountNotFound;
import bookkeeper.telegram.shared.exception.AccountTransactionNotFound;
import bookkeeper.telegram.shared.exception.HandlerInterruptException;

import javax.inject.Inject;

import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseMessage;


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

        transaction.setAccount(account);
        request.editMessage(getResponseMessage(transaction), getResponseKeyboard(transaction));
        return true;
    }
}
