package bookkeeper.telegram.scenario;

import bookkeeper.telegram.FakeApp;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class ViewAnnualWorthScenarioTest {
    @Test
    void emptyWorth() {
        FakeApp.session().sendText("/annual").expectStartsWith("`Jan  0.00M (+0.00K)");
    }

    @Test
    void nonEmptyWorth() {
        var januaryTransactionInput = "Покупка 01.01.%s. Карта *0964. 123456 RUB. MOS.TRANSP. Доступно 499.28 RUB".formatted(LocalDate.now().getYear());
        FakeApp
            .session()
            .sendText(januaryTransactionInput)
            .sendText("/annual")
            .expectStartsWith("`Jan -0.12M (-123.K)");
    }
}
