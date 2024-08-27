package bookkeeper.telegram.scenario;

import bookkeeper.dao.entity.AccountTransaction;
import bookkeeper.resolverAnnotations.Expenditure;
import bookkeeper.resolverAnnotations.PreviousMonth;
import bookkeeper.resolverAnnotations.Raw;
import bookkeeper.telegram.BookkeeperParameterResolver;
import bookkeeper.telegram.FakeSession;
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

    @SuppressWarnings("unused")
    @Test
    void findByText(
        @Raw(raw = "фигня 20 RUB")
        AccountTransaction transaction,
        FakeSession session
    ) {
        session.sendText("фигня").expectStartsWith("Найдена 1 запись");
    }

    @SuppressWarnings("unused")
    @Test
    void findByExpenditure(
        @Expenditure(value = bookkeeper.enums.Expenditure.BANKING)
        AccountTransaction transaction,
        FakeSession session
    ) {
        session.sendText("кэшбек").expectStartsWith("Найдена 1 запись");
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
