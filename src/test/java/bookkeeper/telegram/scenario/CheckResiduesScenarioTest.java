package bookkeeper.telegram.scenario;

import bookkeeper.dao.entity.AccountTransaction;
import bookkeeper.resolverAnnotations.Amount;
import bookkeeper.resolverAnnotations.Currency;
import bookkeeper.resolverAnnotations.Raw;
import bookkeeper.telegram.BookkeeperParameterResolver;
import bookkeeper.telegram.FakeSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BookkeeperParameterResolver.class)
class CheckResiduesScenarioTest {
    @SuppressWarnings("unused")
    @Test
    void checkSingleAccountResidue(
        @Amount(amount = 1000)
        AccountTransaction tx1,
        @Amount(amount = -300)
        @Raw(raw = "Покупка, карта *8888. 300 RUB. Blabla. Доступно 100 RUB")
        AccountTransaction tx2,
        FakeSession session
    ) {
        session.sendText("/check_residues").expectContains("Разница: -600");
    }

    @SuppressWarnings("unused")
    @Test
    void checkMultipleAccountResidue(
        @Amount(amount = 100)
        @Raw(raw = "Пополнение, счет RUB. 100 RUB. Доступно 100 RUB")
        AccountTransaction tx1,
        @Amount(amount = 10)
        @Currency(currency = "USD")
        @Raw(raw = "Пополнение, счет USD. 10 USD. Доступно 10 USD")
        AccountTransaction tx2,
        @Amount(amount = 1)
        @Currency(currency = "GEL")
        @Raw(raw = "Бинго 1 GEL")
        AccountTransaction tx3,
        FakeSession session
    ) {
        session.sendText("/check_residues").
            expectContains("Разница: 0 $").
            expectContains("Разница: 0 ₽");
    }

    @Test
    void checkEmptyAccountResidue(FakeSession session) {
        session.sendText("/check_residues").expectContains("Нет данных");
    }
}
