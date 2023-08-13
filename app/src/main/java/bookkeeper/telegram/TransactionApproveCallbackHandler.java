package bookkeeper.telegram;

import bookkeeper.repositories.AccountTransactionRepository;
import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.telegram.callbacks.TransactionApproveCallback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.util.stream.Collectors;

import static bookkeeper.telegram.responses.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.responses.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user approves transaction.
 */
public class TransactionApproveCallbackHandler extends AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    TransactionApproveCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountTransactionRepository transactionRepository) {
        super(bot, telegramUserRepository);
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handle "Approve transaction" click: mark given transaction as approved.
     */
    @Override
    Boolean handle(Update update) {
        var callbackMessage = getCallbackMessage(update);
        if (!(callbackMessage instanceof TransactionApproveCallback))
            return false;

        var cm = ((TransactionApproveCallback) callbackMessage);
        var transaction = transactionRepository.get(cm.getTransactionId());
        var pendingTransactionsCount = cm.getPendingTransactionIds().size();

        transactionRepository.approve(transaction);

        if (pendingTransactionsCount == 0) {
            editMessage(update, getResponseKeyboard(transaction));
        }
        else {
            var nextPendingTransaction = transactionRepository.get(cm.getPendingTransactionIds().get(0));
            var remainingTransactionIds = cm.getPendingTransactionIds().stream().skip(1).collect(Collectors.toList());
            editMessage(update, getResponseMessage(nextPendingTransaction, remainingTransactionIds.size()), getResponseKeyboard(nextPendingTransaction, remainingTransactionIds));
        }

        return true;
    }
}
