package bookkeeper.telegram.scenarios.refine;

import bookkeeper.services.repositories.AccountTransactionRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.TransactionResponseFactory;
import bookkeeper.telegram.shared.AbstractHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;


/**
 * Scenario: user approves transactions in bulk.
 */
public class TransactionApproveBulkCallbackHandler extends AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    public TransactionApproveBulkCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountTransactionRepository transactionRepository) {
        super(bot, telegramUserRepository);
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handle "Approve transactions" click: mark given transactions as approved.
     */
    @Override
    public Boolean handle(Update update) {
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
