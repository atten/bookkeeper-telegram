package bookkeeper.telegram.scenarios.editTransactions;

import bookkeeper.services.repositories.AccountTransactionRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.CallbackMessageRegistry;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import static bookkeeper.telegram.shared.TransactionResponseFactory.*;


/**
 * Scenario: user cancels transaction addition.
 */
public class RemoveTransactionCallbackHandler extends AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    public RemoveTransactionCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountTransactionRepository transactionRepository) {
        super(bot, telegramUserRepository);
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handle "Cancel" click: delete given transaction.
     */
    @Override
    public Boolean handle(Update update) {
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(update);
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof RemoveTransactionCallback))
            return false;

        var cm = (RemoveTransactionCallback) callbackMessage.get();
        var transaction = transactionRepository.find(cm.getTransactionId());

        if (transaction == null) {
            logger.warn(String.format("transaction id=%s not found!", cm.getTransactionId()));
            return false;
        }

        transactionRepository.remove(transaction);
        editMessage(update, strikeoutMessage(getResponseMessage(transaction)));
        return true;
    }

    private String strikeoutMessage(String message) {
        return String.format("<del>%s</del>", message);
    }
}
