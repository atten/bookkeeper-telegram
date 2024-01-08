package bookkeeper.telegram.scenario.addTransaction;

import bookkeeper.enums.HandlerPriority;
import bookkeeper.exception.AccountTransactionNotParsed;
import bookkeeper.service.registry.TransactionParserRegistry;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;
import bookkeeper.service.telegram.StringUtils;

import java.util.Optional;
import java.util.StringJoiner;

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
     * 1. Take telegram message contains one or more raw transactions.
     * 2. Transform them to AccountTransaction and put to AccountTransactionRepository.
     * 3. In case of partial success (some records have been parsed, some haven't), don't save them and display error message.
     */
    public Boolean handle(Request request) throws AccountTransactionNotParsed {
        if (request.getMessageText().isEmpty())
            return false;

        var rawMessages = request.getMessageText().split("\n");
        var results = transactionParserRegistry.parseMultiple(rawMessages, request.getTelegramUser());

        // skip empty transactions
        var transactions = results
            .stream()
            .map(TransactionParserRegistry.TransactionParseResult::transaction)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(transaction -> !transaction.isEmpty())
            .toList();

        // detect errors
        var errors = results
            .stream()
            .map(TransactionParserRegistry.TransactionParseResult::error)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();

        // handle errors
        if (!errors.isEmpty()) {
            if (transactions.isEmpty())
                // failed to parse all messages
                return false;

            // partial success: display error message
            var summary = new StringJoiner("\n");
            var counter = 1;

            summary.add(String.format("%s / %s строк не распознано:", errors.size(), rawMessages.length));

            for (var result : results) {
                if (result.error().isPresent()) {
                    summary.add(String.format("%s %s: %s", StringUtils.getNumberIcon(counter), result.rawMessage(), result.error().get().getLocalizedMessage()));
                }
                counter++;
            }

            throw new AccountTransactionNotParsed(summary.toString());
        }

        transactions.forEach(transactionRepository::save);
        request.replyMessage(getResponseMessage(transactions), getResponseKeyboard(transactions));
        return true;
    }
}
