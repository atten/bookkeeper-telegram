package bookkeeper.telegram.scenarios.edit;

import bookkeeper.services.repositories.AccountTransactionRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.CallbackMessageRegistry;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.util.stream.Collectors;

import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user approves transaction.
 */
public class TransactionApproveCallbackHandler extends AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    public TransactionApproveCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountTransactionRepository transactionRepository) {
        super(bot, telegramUserRepository);
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handle "Approve transaction" click: mark given transaction as approved.
     */
    @Override
    public Boolean handle(Update update) {
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(update);
        if (!(callbackMessage instanceof TransactionApproveCallback))
            return false;

        var cm = ((TransactionApproveCallback) callbackMessage);
        var transaction = transactionRepository.get(cm.getTransactionId());
        var pendingTransactionsCount = cm.getPendingTransactionIds().size();

        if (transaction == null) {
            logger.warn(String.format("transaction id=%s not found!", cm.getTransactionId()));
            return false;
        }

        transactionRepository.approve(transaction);

        if (pendingTransactionsCount == 0) {
            editMessage(update, getResponseKeyboard(transaction));
        }
        else {
            var nextPendingTransactionId = cm.getPendingTransactionIds().get(0);
            var nextPendingTransaction = transactionRepository.get(nextPendingTransactionId);

            if (nextPendingTransaction == null) {
                logger.warn(String.format("transaction id=%s not found!", nextPendingTransactionId));
                return false;
            }

            var remainingTransactionIds = cm.getPendingTransactionIds().stream().skip(1).collect(Collectors.toList());
            editMessage(update, getResponseMessage(nextPendingTransaction, remainingTransactionIds.size()), getResponseKeyboard(nextPendingTransaction, remainingTransactionIds));
        }

        return true;
    }
}
