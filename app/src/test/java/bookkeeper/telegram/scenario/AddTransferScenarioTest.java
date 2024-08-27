package bookkeeper.telegram.scenario;

import bookkeeper.dao.entity.Account;
import bookkeeper.resolverAnnotations.Name;
import bookkeeper.telegram.BookkeeperParameterResolver;
import bookkeeper.telegram.FakeSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BookkeeperParameterResolver.class)
class AddTransferScenarioTest {
    @Test
    void justCommand(FakeSession session) {
        session
            .sendText("/new_transfer")
            .expectContains("Пример");
    }

    @SuppressWarnings("unused")
    @Test
    void addTransferWithSameCurrency(
        @Name(name = "Account1")
        Account account1,
        @Name(name = "Account2")
        Account account2,
        FakeSession session
    ) {
        session
            .sendText("/new_transfer 1000 rub")
            .expectContains("Выберите счёт")
            .pressButton("Account1")
            .expectContains("Выберите счёт")
            .pressButton("Account2")
            .expectContains("Выберите месяц")
            .pressButton("Готово")
            .expectContains("Account1")
            .expectContains("Account2")
            .expectContains("создан");
    }
}
