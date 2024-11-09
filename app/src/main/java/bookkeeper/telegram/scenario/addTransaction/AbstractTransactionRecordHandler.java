package bookkeeper.telegram.scenario.addTransaction;

import bookkeeper.dao.AccountTransactionRepository;
import bookkeeper.exception.AccountTransactionNotParsed;
import bookkeeper.service.registry.TransactionParserRegistry;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;
import bookkeeper.service.telegram.StringUtils;

import java.util.List;
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

    /**
     * Parse record list and display summary.
     * 1. Take telegram message contains one or more raw transactions.
     * 2. Transform them to AccountTransaction and put to AccountTransactionRepository.
     * 3. Save all successfully parsed transactions.
     * 4. In case of partial success (some records have been parsed, some haven't), display warning message.
     * 5. In case of duplicated transactions found, display warning message.
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
        if (!errors.isEmpty() && transactions.isEmpty()) {
            // failed to parse all messages -> skip handling
            return false;
        }

        // partial success -> display warning message
        var unidentifiedMessage = getUnidentifiedLinesWarningMessage(results);

        // detect duplicates
        var duplicatesMessage = getDuplicatesWarningMessage(results);

        transactions.forEach(transactionRepository::save);
        request.replyMessage(getResponseMessage(transactions), getResponseKeyboard(transactions));
        unidentifiedMessage.ifPresent(request::replyMessage);
        duplicatesMessage.ifPresent(request::replyMessage);
        return true;
    }

    private static Optional<String> getUnidentifiedLinesWarningMessage(List<TransactionParserRegistry.TransactionParseResult> results) {
        var errorStrings = new StringJoiner("\n");
        var errorsCount = 0;
        var messageCount = 1;

        for (var result : results) {
            if (result.error().isPresent()) {
                errorStrings.add("%s `%s`: %s".formatted(StringUtils.getNumberIcon(messageCount), result.rawMessage(), result.error().get().getLocalizedMessage()));
                errorsCount++;
            }
            messageCount++;
        }

        if (errorsCount == 0) {
            return Optional.empty();
        }

        var summary = "%s %s / %s строк не распознано:".formatted(StringUtils.ICON_WARNING, errorsCount, results.size());
        return Optional.of(summary + "\n" + errorStrings);
    }

    private Optional<String> getDuplicatesWarningMessage(List<TransactionParserRegistry.TransactionParseResult> results) {
        var duplicateStrings = new StringJoiner("\n");
        var duplicatesCount = 0;
        var messageCount = 1;
        var monthOffset = 0;

        for (var result : results) {
            if (result.transaction().isPresent()) {
                var fuzzyDuplicates = transactionRepository.findByRawText(
                    result.rawMessage(),
                    monthOffset,
                    result.transaction().get().getAccount().getTelegramUser()
                );
                var ignoreCaseDuplicates = fuzzyDuplicates
                    .stream()
                    .filter(duplicate -> duplicate.getRaw().equalsIgnoreCase(result.rawMessage()))
                    .toList();

                if (!ignoreCaseDuplicates.isEmpty()) {
                    var message = "%s `%s`: %s".formatted(
                        StringUtils.getNumberIcon(messageCount),
                        result.rawMessage(),
                        StringUtils.pluralizeTemplate(
                            ignoreCaseDuplicates.size(),
                            "%s дубль",
                            "%s дубля",
                            "%s дублей"
                        )
                    );
                    duplicateStrings.add(message);
                    duplicatesCount++;
                }
            }
            messageCount++;
        }

        if (duplicatesCount == 0) {
            return Optional.empty();
        }

        var summary = "%s Обнаружены повторяющиеся записи за %s:".formatted(StringUtils.ICON_WARNING, StringUtils.getMonthName(monthOffset));
        return Optional.of(summary + "\n\n" + duplicateStrings);
    }
}
