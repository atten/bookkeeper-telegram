package bookkeeper.telegram.scenarios.editTransactions;

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
 * Scenario: user edits transactions in bulk.
 */
public class EditTransactionBulkCallbackHandler extends AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    public EditTransactionBulkCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountTransactionRepository transactionRepository) {
        super(bot, telegramUserRepository);
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Boolean handle(Update update) {
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(update);
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof EditTransactionBulkCallback))
            return false;

        var cm = (EditTransactionBulkCallback) callbackMessage.get();
        var transaction = transactionRepository.find(cm.getTransactionIds().get(0));
        var pendingTransactionIds = cm.getTransactionIds().stream().skip(1).collect(Collectors.toList());

        if (transaction == null) {
            logger.warn(String.format("transaction id=%s not found!", cm.getTransactionIds().get(0)));
            return false;
        }

        editMessage(update, getResponseMessage(transaction, pendingTransactionIds.size()), getResponseKeyboard(transaction, pendingTransactionIds));
        return true;
    }
}
