package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.service.repository.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.CallbackMessageRegistry;
import bookkeeper.telegram.shared.exception.AccountTransactionNotFound;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import javax.inject.Inject;
import java.util.stream.Collectors;

import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user approves transaction.
 */
class ApproveTransactionCallbackHandler extends AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    @Inject
    ApproveTransactionCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountTransactionRepository transactionRepository) {
        super(bot, telegramUserRepository);
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handle "Approve transaction" click: mark given transaction as approved.
     */
    @Override
    public Boolean handle(Update update) throws AccountTransactionNotFound {
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(update);
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof ApproveTransactionCallback cm))
            return false;

        var transaction = transactionRepository.get(cm.getTransactionId()).orElseThrow(() -> new AccountTransactionNotFound(cm.getTransactionId()));
        var pendingTransactionsCount = cm.getPendingTransactionIds().size();

        transactionRepository.approve(transaction);

        if (pendingTransactionsCount == 0) {
            // show summary message for added/edited transactions within batch
            var allAddedTransactions = transactionRepository.findByCreatedAt(transaction.getCreatedAt(), getTelegramUser(update));
            editMessage(update, getResponseMessage(allAddedTransactions), getResponseKeyboard(allAddedTransactions));
        }
        else {
            var nextPendingTransactionId = cm.getPendingTransactionIds().get(0);
            var nextPendingTransaction = transactionRepository.get(nextPendingTransactionId).orElseThrow(() -> new AccountTransactionNotFound(nextPendingTransactionId));
            var remainingTransactionIds = cm.getPendingTransactionIds().stream().skip(1).collect(Collectors.toList());
            editMessage(update, getResponseMessage(nextPendingTransaction, remainingTransactionIds.size()), getResponseKeyboard(nextPendingTransaction, remainingTransactionIds));
        }

        return true;
    }
}
