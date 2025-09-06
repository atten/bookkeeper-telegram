package bookkeeper.telegram.scenario.searchTransactions;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

@Getter
class SearchTransactionsByRawMessageCallback extends CallbackMessage {
    private final String searchQuery;
    private final int monthOffset;

    SearchTransactionsByRawMessageCallback(String searchQuery, int monthOffset) {
        this.searchQuery = searchQuery;
        this.monthOffset = monthOffset;
    }

}
