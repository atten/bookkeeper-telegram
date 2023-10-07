package bookkeeper.telegram.scenario;

import bookkeeper.entity.AccountTransaction;
import bookkeeper.telegram.BookkeeperParameterResolver;
import bookkeeper.telegram.FakeSession;
import bookkeeper.resolverAnnotations.PreviousMonth;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.List;

@ExtendWith(BookkeeperParameterResolver.class)
class SearchTransactionsScenarioTest {
    private static Collection<String> emptyInputs() {
        return List.of(
            "123",
            "Обезьяна 1 BTC"
        );
    }

    @ParameterizedTest
    @MethodSource("emptyInputs")
    void emptyResult(String input, FakeSession session) {
        session.sendText(input).expectStartsWith("Найдено 0 записей");
    }

    @Test
    void nonEmptyResult(FakeSession session) {
        session.sendText("еда 20 RUB");
        session.sendText("еда").expectStartsWith("Найдена 1 запись");
    }

    @Test
    void previousMonth(@PreviousMonth AccountTransaction transaction, FakeSession session) {
        var searchText = transaction.getRaw().split(" ")[0];
        session
            .sendText(searchText)
            .expectStartsWith("Найдено 0 записей")
            .pressButton("◀")
            .expectStartsWith("Найдена 1 запись");
    }
}
