package bookkeeper.telegram.scenarios.editTransactions;

import bookkeeper.services.repositories.AccountTransactionRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.CallbackMessageRegistry;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.time.temporal.ChronoUnit;

import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user changes transaction month.
 */
public class ShiftTransactionMonthCallbackHandler extends AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    public ShiftTransactionMonthCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountTransactionRepository transactionRepository) {
        super(bot, telegramUserRepository);
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handle "Shift transaction month" click: subtract 1 month from current transaction timestamp.
     */
    @Override
    public Boolean handle(Update update) {
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(update);
        if (!(callbackMessage instanceof ShiftTransactionMonthCallback))
            return false;

        var cm = ((ShiftTransactionMonthCallback) callbackMessage);
        var transaction = transactionRepository.get(cm.getTransactionId());
        var pendingTransactionsCount = cm.getPendingTransactionIds().size();

        if (transaction == null) {
            logger.warn(String.format("transaction id=%s not found!", cm.getTransactionId()));
            return false;
        }

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
