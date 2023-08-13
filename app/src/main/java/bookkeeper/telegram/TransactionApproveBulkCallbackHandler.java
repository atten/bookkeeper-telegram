package bookkeeper.telegram;

import bookkeeper.repositories.AccountTransactionRepository;
import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.telegram.callbacks.TransactionApproveBulkCallback;
import bookkeeper.telegram.responses.TransactionResponseFactory;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;


/**
 * Scenario: user approves transactions in bulk.
 */
public class TransactionApproveBulkCallbackHandler extends AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    TransactionApproveBulkCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountTransactionRepository transactionRepository) {
        super(bot, telegramUserRepository);
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handle "Approve transactions" click: mark given transactions as approved.
     */
    @Override
    Boolean handle(Update update) {
        var callbackMessage = getCallbackMessage(update);
        if (!(callbackMessage instanceof TransactionApproveBulkCallback))
            return false;

        var cm = ((TransactionApproveBulkCallback) callbackMessage);
        var transactions = transactionRepository.getList(cm.getTransactionIds());

        transactions.forEach(transactionRepository::approve);

        editMessage(update, TransactionResponseFactory.getResponseKeyboard(transactions));
        return true;
    }
}
