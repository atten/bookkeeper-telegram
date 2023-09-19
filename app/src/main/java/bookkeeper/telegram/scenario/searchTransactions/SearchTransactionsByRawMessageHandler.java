package bookkeeper.telegram.scenario.searchTransactions;

import bookkeeper.entity.TelegramUser;
import bookkeeper.enums.HandlerPriority;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.Request;
import bookkeeper.telegram.shared.StringUtil;
import bookkeeper.telegram.shared.exception.HandlerInterruptException;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import javax.inject.Inject;
import java.util.StringJoiner;

import static bookkeeper.telegram.shared.StringUtil.pluralizeTemplate;


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
        var isCallback = request.getMessageText().isEmpty();

        if (searchQuery.isEmpty()) {
            return false;
        }
        var response = getResponse(searchQuery, monthOffset, request.getTelegramUser());
        if (response.isEmpty()) {
            // skip handling in case of empty search output
            return false;
        }
        if (isCallback) {
            request.editMessage(response, getKeyboard(searchQuery, monthOffset));
        } else {
            request.replyMessage(response, getKeyboard(searchQuery, monthOffset));
        }
        return true;
    }

    private SearchTransactionsByRawMessageCallback getInputData(Request request) {
        var callbackMessage = request.getCallbackMessage();
        if (callbackMessage.isPresent() && callbackMessage.get() instanceof SearchTransactionsByRawMessageCallback cm)
            return cm;
        return new SearchTransactionsByRawMessageCallback(request.getMessageText(), 0);
    }

    private String getResponse(String searchQuery, int monthOffset, TelegramUser user) {
        var searchResult = transactionRepository.findByRawText(searchQuery, monthOffset, user);
        var response = new StringJoiner("\n");
        response.add("Найдено %s записей за %s".formatted(searchResult.size(), StringUtil.getMonthYearShort(monthOffset)));

        int pageSize = Math.min(10, searchResult.size());
        for (int counter = 1; counter <= pageSize; counter++) {
            var transaction = searchResult.remove(0);
            response.add(String.format("%s. `%s` (%s)", counter, transaction.getRaw(), transaction.getExpenditure().getVerboseName()));
        }

        if (searchResult.size() > 0) {
            response.add(String.format("(ещё %s)...", pluralizeTemplate(
                searchResult.size(),
                "%s запись",
                "%s записи",
                "%s записей"
            )));
        }

        return response.toString();
    }

    private InlineKeyboardMarkup getKeyboard(String searchQuery, int monthOffset) {
        return new InlineKeyboardMarkup().addRow(
            new SearchTransactionsByRawMessageCallback(searchQuery, monthOffset - 1).asPrevMonthButton(monthOffset - 1),
            new SearchTransactionsByRawMessageCallback(searchQuery, monthOffset + 1).asNextMonthButton(monthOffset + 1)
        );
    }

    @Override
    public HandlerPriority getPriority() {
        return HandlerPriority.LOWEST_MESSAGE;
    }
}
