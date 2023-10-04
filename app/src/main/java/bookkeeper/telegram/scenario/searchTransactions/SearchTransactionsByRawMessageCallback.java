package bookkeeper.telegram.scenario.searchTransactions;

import bookkeeper.service.telegram.CallbackMessage;
import lombok.Getter;

class SearchTransactionsByRawMessageCallback extends CallbackMessage {
    @Getter
    private final String searchQuery;
    @Getter
    private final int monthOffset;

    SearchTransactionsByRawMessageCallback(String searchQuery, int monthOffset) {
        this.searchQuery = searchQuery;
        this.monthOffset = monthOffset;
    }

}
