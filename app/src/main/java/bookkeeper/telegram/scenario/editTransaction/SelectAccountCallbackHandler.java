package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.entity.AccountTransaction;
import bookkeeper.entity.TelegramUser;
import bookkeeper.exception.AccountTransactionNotFound;
import bookkeeper.service.repository.AccountRepository;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.KeyboardUtils;
import bookkeeper.telegram.shared.Request;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import javax.inject.Inject;
import java.util.List;


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

        var transaction = transactionRepository.get(cm.getTransactionId()).orElseThrow(() -> new AccountTransactionNotFound(cm.getTransactionId()));

        request.editMessage(getResponseKeyboard(transaction, request.getTelegramUser(), cm.getPendingTransactionIds()));
        return true;
    }

    private InlineKeyboardMarkup getResponseKeyboard(AccountTransaction transaction, TelegramUser user, List<Long> pendingTransactionIds) {
        var buttons = accountRepository
            .filter(user, transaction.currency())
            .stream()
            .filter(account -> transaction.getAccount().getId() != account.getId())
            // prepare buttons with expenditures selector
            .map(account -> new SwitchAccountCallback(transaction.getId(), account.getId()).setPendingTransactionIds(pendingTransactionIds).asButton(account.getName()))
            .toList();
        return KeyboardUtils.createMarkupWithFixedColumns(buttons, 3);
    }
}
