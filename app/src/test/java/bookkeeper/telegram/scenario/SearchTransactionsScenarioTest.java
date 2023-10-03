package bookkeeper.telegram.scenario;

import bookkeeper.telegram.FakeApp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.List;

class SearchTransactionsScenarioTest {
    private static Collection<String> emptyInputs() {
        return List.of(
            "123",
            "Обезьяна 1 BTC"
        );
    }

    @ParameterizedTest
    @MethodSource("emptyInputs")
    void emptyResult(String input) {
        var session = FakeApp.session();
        session.sendText(input).expectStartsWith("Найдено 0 записей");
    }

    @Test
    void nonEmptyResult() {
        var session = FakeApp.session();
        session.sendText("еда 20 RUB");
        session.sendText("еда").expectStartsWith("Найдена 1 запись");
    }
}
