package bookkeeper.telegram.scenario.searchTransactions;

import bookkeeper.entity.TelegramUser;
import bookkeeper.enums.HandlerPriority;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;
import bookkeeper.service.telegram.StringUtils;
import bookkeeper.exception.HandlerInterruptException;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static bookkeeper.service.telegram.StringUtils.*;


/**
 * Scenario: User searches transactions.
 */
class SearchTransactionsByRawMessageHandler implements AbstractHandler {
    private final AccountTransactionRepository transactionRepository;

    @Inject
    SearchTransactionsByRawMessageHandler(AccountTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Boolean handle(Request request) throws HandlerInterruptException {
        var inputCallback = getInputData(request);

        var searchQuery = inputCallback.getSearchQuery();
        var monthOffset = inputCallback.getMonthOffset();
        var isText = !request.getMessageText().isEmpty();  // determine whether text was actually provided

        if (searchQuery.isEmpty()) {
            return false;
        }
        var response = getResponse(searchQuery, monthOffset, request.getTelegramUser());
        if (isText) {
            request.replyMessage(response, getKeyboard(searchQuery, monthOffset));
        } else {
            request.editMessage(response, getKeyboard(searchQuery, monthOffset));
        }
        return true;
    }

    private SearchTransactionsByRawMessageCallback getInputData(Request request) {
        if (request.getCallbackMessage().orElse(null) instanceof SearchTransactionsByRawMessageCallback cm)
            return cm;
        return new SearchTransactionsByRawMessageCallback(request.getMessageText(), 0);
    }

    private String getResponse(String searchQuery, int monthOffset, TelegramUser user) {
        var searchResult = transactionRepository.findByRawText(searchQuery, monthOffset, user);

        var amountByCurrency = new HashMap<Currency, BigDecimal>();
        for (var transaction : searchResult) {
            var key = transaction.currency();
            amountByCurrency.putIfAbsent(key, BigDecimal.ZERO);
            amountByCurrency.merge(key, transaction.getAmount(), BigDecimal::add);
        }
        var amountByCurrencyVerbose = amountByCurrency.entrySet().stream().map(entry -> getAmount(entry.getValue(), entry.getKey())).collect(Collectors.joining(", "));

        var response = new StringJoiner("\n\n");
        response.add("%s стоимостью %s за %s:".formatted(getItemsVerbose(searchResult.size()), amountByCurrencyVerbose, StringUtils.getMonthYearShort(monthOffset)));

        int pageSize = Math.min(10, searchResult.size());
        for (int counter = 1; counter <= pageSize; counter++) {
            var transaction = searchResult.remove(0);
            var dateString = transaction.date().format(DateTimeFormatter.ofPattern("dd.MM"));
            response.add(
                String.format(
                    "%s %s. %s. %s\n`%s`",
                    getNumberIcon(counter),
                    dateString,
                    transaction.getExpenditure().getVerboseName(),
                    getAmount(transaction),
                    transaction.getRaw()
                )
            );
        }

        if (searchResult.size() > 0) {
            response.add(String.format("(ещё %s)...", getItemsVerbose(searchResult.size())));
        }

        return response.toString();
    }

    private String getItemsVerbose(int count) {
        return pluralizeTemplate(count,"Найдена %s запись","Найдены %s записи","Найдено %s записей");
    }

    private InlineKeyboardMarkup getKeyboard(String searchQuery, int monthOffset) {
        return new InlineKeyboardMarkup().addRow(
            new SearchTransactionsByRawMessageCallback(searchQuery, monthOffset - 1).asPrevMonthButton(monthOffset - 1),
            new SearchTransactionsByRawMessageCallback(searchQuery, monthOffset + 1).asNextMonthButton(monthOffset + 1)
        );
    }

    /**
     * Use finder in the last place among other handlers
     * because it is very tolerant to text input and handles almost all of it.
     */
    @Override
    public HandlerPriority getPriority() {
        return HandlerPriority.LOWEST_MESSAGE;
    }
}
