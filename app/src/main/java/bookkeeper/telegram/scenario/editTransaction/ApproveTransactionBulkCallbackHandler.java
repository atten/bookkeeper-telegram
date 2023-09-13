package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.service.repository.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.service.registry.CallbackMessageRegistry;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import javax.inject.Inject;

import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseKeyboard;


/**
 * Scenario: user approves transactions in bulk.
 */
class ApproveTransactionBulkCallbackHandler extends AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    @Inject
    ApproveTransactionBulkCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountTransactionRepository transactionRepository) {
        super(bot, telegramUserRepository);
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handle "Approve transactions" click: mark given transactions as approved.
     */
    @Override
    public Boolean handle(Update update) {
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(update);
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof ApproveTransactionBulkCallback cm))
            return false;

        var transactions = transactionRepository.findByIds(cm.getTransactionIds());

        transactions.forEach(transactionRepository::approve);

        editMessage(update, getResponseKeyboard(transactions));
        return true;
    }
}
