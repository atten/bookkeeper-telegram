package bookkeeper.telegram.scenarios.edit;

import bookkeeper.services.repositories.AccountTransactionRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.util.stream.Collectors;

import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user edits transactions in bulk.
 */
public class TransactionEditBulkCallbackHandler extends AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    public TransactionEditBulkCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountTransactionRepository transactionRepository) {
        super(bot, telegramUserRepository);
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Boolean handle(Update update) {
        var callbackMessage = getCallbackMessage(update);
        if (!(callbackMessage instanceof TransactionEditBulkCallback))
            return false;

        var cm = ((TransactionEditBulkCallback) callbackMessage);
        var transaction = transactionRepository.get(cm.getTransactionIds().get(0));
        var pendingTransactionIds = cm.getTransactionIds().stream().skip(1).collect(Collectors.toList());

        if (transaction == null) {
            logger.warn(String.format("transaction id=%s not found!", cm.getTransactionIds().get(0)));
            return false;
        }

        sendMessage(update, getResponseMessage(transaction, pendingTransactionIds.size()), getResponseKeyboard(transaction, pendingTransactionIds));
        return true;
    }
}
