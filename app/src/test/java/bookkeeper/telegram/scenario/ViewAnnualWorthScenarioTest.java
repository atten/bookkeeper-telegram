package bookkeeper.telegram.scenario;

import bookkeeper.entity.AccountTransaction;
import bookkeeper.resolverAnnotations.Amount;
import bookkeeper.resolverAnnotations.Month;
import bookkeeper.telegram.BookkeeperParameterResolver;
import bookkeeper.telegram.FakeSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BookkeeperParameterResolver.class)
class ViewAnnualWorthScenarioTest {
    @Test
    void emptyWorth(FakeSession session) {
        session.sendText("/annual").expectStartsWith("`Jan  0.00M (+0.00K)");
    }

    @Test
    void nonEmptyWorth(@SuppressWarnings("unused")
                       @Month(month = java.time.Month.JANUARY)
                       @Amount(amount = -123456)
                       AccountTransaction transaction,
                       FakeSession session) {
        session
            .sendText("/annual")
            .expectStartsWith("`Янв -0,12M (-123 K)");
    }
}
