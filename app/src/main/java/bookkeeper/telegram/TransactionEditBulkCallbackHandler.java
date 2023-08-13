package bookkeeper.telegram;

import bookkeeper.repositories.AccountTransactionRepository;
import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.telegram.callbacks.TransactionEditBulkCallback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.util.stream.Collectors;

import static bookkeeper.telegram.responses.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.responses.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user edits transactions in bulk.
 */
public class TransactionEditBulkCallbackHandler extends AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    TransactionEditBulkCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountTransactionRepository transactionRepository) {
        super(bot, telegramUserRepository);
        this.transactionRepository = transactionRepository;
    }

    @Override
    Boolean handle(Update update) {
        var callbackMessage = getCallbackMessage(update);
        if (!(callbackMessage instanceof TransactionEditBulkCallback))
            return false;

        var cm = ((TransactionEditBulkCallback) callbackMessage);
        var transaction = transactionRepository.get(cm.getTransactionIds().get(0));
        var pendingTransactionIds = cm.getTransactionIds().stream().skip(1).collect(Collectors.toList());

        sendMessage(update, getResponseMessage(transaction, pendingTransactionIds.size()), getResponseKeyboard(transaction, pendingTransactionIds));
        return true;
    }
}
