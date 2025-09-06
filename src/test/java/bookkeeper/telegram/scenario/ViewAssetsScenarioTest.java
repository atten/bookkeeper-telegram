package bookkeeper.telegram.scenario;

import bookkeeper.dao.entity.AccountTransaction;
import bookkeeper.resolverAnnotations.Amount;
import bookkeeper.resolverAnnotations.Currency;
import bookkeeper.resolverAnnotations.PreviousMonth;
import bookkeeper.telegram.BookkeeperParameterResolver;
import bookkeeper.telegram.FakeSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BookkeeperParameterResolver.class)
class ViewAssetsScenarioTest {
    @Test
    void emptyAssets(FakeSession session) {
        session
            .sendText("/assets")
            .expectContains("Сводка по непустым счетам на конец")
            .expectContains("Курс")
            .expectContains("Итог");
    }

    @SuppressWarnings("unused")
    @Test
    void nonEmptyAssets(
        @Amount(amount = -30000)
        AccountTransaction transaction,
        @Amount(amount = -105.9)
        @Currency(currency = "USD")
        AccountTransaction transaction2,
        FakeSession session) {
        session
            .sendText("/assets")
            .expectContains("-105,90 $")
            .expectContains("-30 000,00 ₽")
            .expectContains("-33 214,07 ₽");
    }

    @Test
    void prevMonthAssets(
        @SuppressWarnings("unused")
        @PreviousMonth
        @Amount(amount = 1000)
        AccountTransaction transaction,
        FakeSession session) {
        session
            .sendText("/assets")
            .pressButton("◀")
            .expectContains("1 000,00 ₽ | 100,0%");
    }
}
