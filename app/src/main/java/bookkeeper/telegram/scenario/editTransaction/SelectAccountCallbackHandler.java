package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.dao.entity.Account;
import bookkeeper.dao.repository.AccountRepository;
import bookkeeper.dao.repository.AccountTransactionRepository;
import bookkeeper.exception.AccountTransactionNotFound;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.KeyboardUtils;
import bookkeeper.service.telegram.Request;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import javax.inject.Inject;


/**
 * Scenario: user changes transaction account.
 */
class SelectAccountCallbackHandler implements AbstractHandler {
    private final AccountRepository accountRepository;
    private final AccountTransactionRepository transactionRepository;

    @Inject
    SelectAccountCallbackHandler(AccountRepository accountRepository, AccountTransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handle "Pick Account" button click: display suitable Accounts list for given AccountTransaction
     */
    public Boolean handle(Request request) throws AccountTransactionNotFound {
        if (!(request.getCallbackMessage().orElse(null) instanceof SelectAccountCallback cm))
            return false;

        request.editMessage(getResponseKeyboard(request, cm));
        return true;
    }

    private InlineKeyboardMarkup getResponseKeyboard(Request request, SelectAccountCallback cm) throws AccountTransactionNotFound {
        var transaction = transactionRepository.get(cm.getTransactionId()).orElseThrow(() -> new AccountTransactionNotFound(cm.getTransactionId()));

        var accounts = accountRepository.filter(request.getTelegramUser(), transaction.currency());
        var buttons = accounts
            .stream()
            .filter(Account::isVisible)
            // prepare buttons with account selector
            .map(account -> new SwitchAccountCallback(transaction.getId(), account.getId()).setTransactionIds(cm.getAllTransactionIds(), cm.getPendingTransactionIds()).asAccountButton(account))
            .toList();
        return KeyboardUtils.createMarkupWithFixedColumns(buttons, 3);
    }
}
