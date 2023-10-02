package bookkeeper.telegram.scenario;

import bookkeeper.telegram.FakeApp;
import org.junit.jupiter.api.Test;

class ViewAnnualWorthScenarioTest {
    @Test
    void getEmptyWorth() {
        FakeApp.session().sendText("/annual").expectStartsWith("`Jan  0.00M (+0.00K)");
    }
}
