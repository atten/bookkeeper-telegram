package bookkeeper.telegram.scenarios.editTransactions;

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
public class ApproveTransactionBulkCallbackHandler extends AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    public ApproveTransactionBulkCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountTransactionRepository transactionRepository) {
        super(bot, telegramUserRepository);
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handle "Approve transactions" click: mark given transactions as approved.
     */
    @Override
    public Boolean handle(Update update) {
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(update);
        if (!(callbackMessage instanceof ApproveTransactionBulkCallback))
            return false;

        var cm = ((ApproveTransactionBulkCallback) callbackMessage);
        var transactions = transactionRepository.findByIds(cm.getTransactionIds());

        transactions.forEach(transactionRepository::approve);

        editMessage(update, getResponseKeyboard(transactions));
        return true;
    }
}
