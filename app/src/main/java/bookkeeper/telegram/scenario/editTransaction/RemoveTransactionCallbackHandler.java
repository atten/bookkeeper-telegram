package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.service.repository.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.service.registry.CallbackMessageRegistry;
import bookkeeper.telegram.shared.exception.AccountTransactionNotFound;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import javax.inject.Inject;

import static bookkeeper.telegram.shared.TransactionResponseFactory.*;


/**
 * Scenario: user cancels transaction addition.
 */
class RemoveTransactionCallbackHandler extends AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    @Inject
    RemoveTransactionCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountTransactionRepository transactionRepository) {
        super(bot, telegramUserRepository);
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handle "Cancel" click: delete given transaction.
     */
    @Override
    public Boolean handle(Update update) throws AccountTransactionNotFound {
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(update);
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof RemoveTransactionCallback cm))
            return false;

        var transaction = transactionRepository.get(cm.getTransactionId()).orElseThrow(() -> new AccountTransactionNotFound(cm.getTransactionId()));
        transactionRepository.remove(transaction);
        editMessage(update, strikeoutMessage(getResponseMessage(transaction)));
        return true;
    }

    private String strikeoutMessage(String message) {
        return String.format("<del>%s</del>", message);
    }
}
