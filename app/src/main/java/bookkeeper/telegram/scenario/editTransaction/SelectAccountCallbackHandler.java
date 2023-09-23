package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.entity.AccountTransaction;
import bookkeeper.entity.TelegramUser;
import bookkeeper.service.repository.AccountRepository;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.Request;
import bookkeeper.telegram.shared.exception.AccountTransactionNotFound;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


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
        var kb = new InlineKeyboardMarkup();
        var groupBy = 3;
        AtomicInteger index = new AtomicInteger(0);

        accountRepository.filter(user, transaction.currency()).stream()
            .filter(account -> transaction.getAccount().getId() != account.getId())
            .map(account ->
                // prepare buttons with expenditures selector
                new SwitchAccountCallback(transaction.getId(), account.getId()).setPendingTransactionIds(pendingTransactionIds).asButton(account.getName())
            ).collect(
                // split to N map items each contains a list of 3 buttons
                Collectors.groupingBy(i -> index.getAndIncrement() / groupBy)
            ).values().forEach ((inlineKeyboardButtons) ->
                // append keyboard rows
                kb.addRow(inlineKeyboardButtons.toArray(InlineKeyboardButton[]::new))
            );
        return kb;
    }
}
