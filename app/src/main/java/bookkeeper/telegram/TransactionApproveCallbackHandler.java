package bookkeeper.telegram;

import bookkeeper.repositories.AccountTransactionRepository;
import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.telegram.callbacks.TransactionApproveCallback;
import bookkeeper.telegram.responses.TransactionResponseFactory;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;


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

        transactionRepository.approve(transaction);

        editMessage(update, TransactionResponseFactory.getResponseKeyboard(transaction));
        return true;
    }
}
