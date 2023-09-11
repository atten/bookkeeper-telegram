package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.service.repository.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.CallbackMessageRegistry;
import bookkeeper.telegram.shared.exception.AccountTransactionNotFound;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import javax.inject.Inject;
import java.time.temporal.ChronoUnit;

import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user changes transaction month.
 */
class ShiftTransactionMonthCallbackHandler extends AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    @Inject
    ShiftTransactionMonthCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountTransactionRepository transactionRepository) {
        super(bot, telegramUserRepository);
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handle "Shift transaction month" click: subtract 1 month from current transaction timestamp.
     */
    @Override
    public Boolean handle(Update update) throws AccountTransactionNotFound {
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(update);
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof ShiftTransactionMonthCallback cm))
            return false;

        var transaction = transactionRepository.get(cm.getTransactionId()).orElseThrow(() -> new AccountTransactionNotFound(cm.getTransactionId()));
        var pendingTransactionsCount = cm.getPendingTransactionIds().size();
        var days = 30 * cm.getMonthOffset();
        transaction.setTimestamp(transaction.getTimestamp().plus(days, ChronoUnit.DAYS));

        if (pendingTransactionsCount == 0) {
            editMessage(update, getResponseMessage(transaction), getResponseKeyboard(transaction));
        }
        else {
            editMessage(update, getResponseMessage(transaction, pendingTransactionsCount), getResponseKeyboard(transaction, cm.getPendingTransactionIds()));
        }

        return true;
    }
}
