package bookkeeper.telegram.scenario;

import bookkeeper.telegram.BookkeeperParameterResolver;
import bookkeeper.telegram.FakeSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BookkeeperParameterResolver.class)
class AddAccountScenarioTest {
    @Test
    void justCommand(FakeSession session) {
        session
            .sendText("/new_account")
            .expectContains("Пример");
    }

    @Test
    void addAccount(FakeSession session) {
        session
            .sendText("/new_account Толстый Кошелёк RUB")
            .expectContains("Готово");
    }
}
