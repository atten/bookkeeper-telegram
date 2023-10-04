package bookkeeper.telegram.scenario;

import bookkeeper.telegram.FakeApp;
import org.junit.jupiter.api.Test;

class AddAccountScenarioTest {
    @Test
    void justCommand() {
        FakeApp
            .session()
            .sendText("/new_account")
            .expectContains("Пример");
    }

    @Test
    void addAccount() {
        FakeApp
            .session()
            .sendText("/new_account Толстый Кошелёк RUB")
            .expectContains("Готово");
    }
}
