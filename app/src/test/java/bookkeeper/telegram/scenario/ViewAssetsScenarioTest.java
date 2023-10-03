package bookkeeper.telegram.scenario;

import bookkeeper.telegram.FakeApp;
import org.junit.jupiter.api.Test;

class ViewAssetsScenarioTest {
    @Test
    void emptyAssets() {
        FakeApp
            .session()
            .sendText("/assets")
            .expectContains("Сводка по непустым счетам на конец")
            .expectContains("Курс")
            .expectContains("Итог");
    }

    @Test
    void nonEmptyAssets() {
        FakeApp
            .session()
            .sendText("еда 30000")
            .sendText("путешествия 105.9 USD")
            .sendText("/assets")
            .expectContains("-105.90 $")
            .expectContains("-30,000.00 RUB")
            .expectContains("-33,177.00 RUB");
    }
}
