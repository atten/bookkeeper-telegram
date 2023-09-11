package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.service.repository.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.CallbackMessageRegistry;
import bookkeeper.telegram.shared.exception.AccountTransactionNotFound;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import javax.inject.Inject;
import java.util.stream.Collectors;

import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user edits transactions in bulk.
 */
class EditTransactionBulkCallbackHandler extends AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    @Inject
    EditTransactionBulkCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountTransactionRepository transactionRepository) {
        super(bot, telegramUserRepository);
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Boolean handle(Update update) throws AccountTransactionNotFound {
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(update);
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof EditTransactionBulkCallback cm))
            return false;

        var transaction = transactionRepository.get(cm.getTransactionIds().get(0)).orElseThrow(() -> new AccountTransactionNotFound(cm.getTransactionIds().get(0)));
        var pendingTransactionIds = cm.getTransactionIds().stream().skip(1).collect(Collectors.toList());
        editMessage(update, getResponseMessage(transaction, pendingTransactionIds.size()), getResponseKeyboard(transaction, pendingTransactionIds));
        return true;
    }
}
