package bookkeeper.telegram.scenario.addTransaction;

import bookkeeper.entity.AccountTransaction;
import bookkeeper.enums.HandlerPriority;
import bookkeeper.service.registry.TransactionParserRegistry;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.Request;

import java.text.ParseException;
import java.util.List;

import static bookkeeper.telegram.scenario.editTransaction.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.scenario.editTransaction.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user stores transactions.
 */
public class AbstractTransactionRecordHandler implements AbstractHandler {
    private final TransactionParserRegistry transactionParserRegistry;
    private final AccountTransactionRepository transactionRepository;

    public AbstractTransactionRecordHandler(AccountTransactionRepository transactionRepository, TransactionParserRegistry transactionParserRegistry) {
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
    public Boolean handle(Request request) {
        if (request.getMessageText().isEmpty())
            return false;

        var rawMessages = request.getMessageText().split("\n");
        List<AccountTransaction> transactions;
        try {
            transactions = transactionParserRegistry.parseMultiple(rawMessages, request.getTelegramUser());
        } catch (ParseException e) {
            // provided sms was not parsed
            return false;
        }

        transactions.forEach(transactionRepository::save);
        request.replyMessage(getResponseMessage(transactions), getResponseKeyboard(transactions));
        return true;

    }

}
