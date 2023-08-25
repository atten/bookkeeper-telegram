package bookkeeper.telegram.scenarios.edit;

import bookkeeper.services.repositories.AccountTransactionRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.CallbackMessageRegistry;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseKeyboard;


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
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(update);
        if (!(callbackMessage instanceof TransactionApproveBulkCallback))
            return false;

        var cm = ((TransactionApproveBulkCallback) callbackMessage);
        var transactions = transactionRepository.getByIds(cm.getTransactionIds());

        transactions.forEach(transactionRepository::approve);

        editMessage(update, getResponseKeyboard(transactions));
        return true;
    }
}
