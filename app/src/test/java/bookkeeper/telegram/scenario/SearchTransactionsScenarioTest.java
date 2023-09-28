package bookkeeper.telegram.scenario;

import bookkeeper.telegram.FakeApp;
import org.junit.jupiter.api.Test;

class SearchTransactionsScenarioTest {
    @Test
    void emptyResult() {
        var session = FakeApp.session();
        session.sendText("123").expectStartsWith("Найдено 0 записей");
    }
}
