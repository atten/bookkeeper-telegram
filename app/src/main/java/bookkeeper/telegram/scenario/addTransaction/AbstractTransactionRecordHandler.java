package bookkeeper.telegram.scenario.addTransaction;

import bookkeeper.entity.AccountTransaction;
import bookkeeper.enums.HandlerPriority;
import bookkeeper.service.registry.TransactionParserRegistry;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.service.repository.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.text.ParseException;
import java.util.List;

import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user stores transactions.
 */
public class AbstractTransactionRecordHandler extends AbstractHandler {
    private final TransactionParserRegistry transactionParserRegistry;
    private final AccountTransactionRepository transactionRepository;

    public AbstractTransactionRecordHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountTransactionRepository transactionRepository, TransactionParserRegistry transactionParserRegistry) {
        super(bot, telegramUserRepository);
        this.transactionParserRegistry = transactionParserRegistry;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public HandlerPriority getPriority() {
        return HandlerPriority.LOW_MESSAGE;
    }

    /**
     * Parse record list and display summary.
     * 1. Take telegram message contains one or more raw transactions
     * 2. Transform them to AccountTransaction and put to AccountTransactionRepository.
     */
    @Override
    public Boolean handle(Update update) {
        if (update.message() == null)
            return false;

        var rawMessages = getMessageText(update).split("\n");
        List<AccountTransaction> transactions;
        try {
            transactions = transactionParserRegistry.parseMultiple(rawMessages, getTelegramUser(update));
        } catch (ParseException e) {
            // provided sms was not parsed
            return false;
        }

        transactions.forEach(transactionRepository::save);
        replyMessage(update, getResponseMessage(transactions), getResponseKeyboard(transactions));
        return true;

    }

}
