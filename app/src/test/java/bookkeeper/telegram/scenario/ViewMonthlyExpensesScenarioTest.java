package bookkeeper.telegram.scenario;

import bookkeeper.dao.entity.AccountTransaction;
import bookkeeper.resolverAnnotations.Amount;
import bookkeeper.resolverAnnotations.Expenditure;
import bookkeeper.resolverAnnotations.PreviousMonth;
import bookkeeper.telegram.BookkeeperParameterResolver;
import bookkeeper.telegram.FakeSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BookkeeperParameterResolver.class)
class ViewMonthlyExpensesScenarioTest {
    @Test
    void emptyExpenses(FakeSession session) {
        session
            .sendText("/expenses")
            .expectContains("Баланс  0");
    }

    @SuppressWarnings("unused")
    @Test
    void nonEmptyExpenses(
        @Amount(amount = -1000)
        AccountTransaction transaction,
        @Amount(amount = 100)
        AccountTransaction transaction2,
        @Amount(amount = 12000)
        @Expenditure(value = bookkeeper.enums.Expenditure.JOB)
        AccountTransaction transaction3,
        FakeSession session) {
        session
            .sendText("/expenses")
            .expectContains("Работа            +12 000")
            .expectContains("Другое             900")
            .expectContains("Расходы -900 ₽")
            .expectContains("Доходы  +12 000 ₽")
            .expectContains("Баланс  +11 100 ₽");
    }

    @Test
    void prevMonthExpenses(
        @SuppressWarnings("unused")
        @PreviousMonth
        @Amount(amount = -1000)
        AccountTransaction transaction,
        FakeSession session) {
        session
            .sendText("/expenses")
            .pressButton("◀")
            .expectContains("Баланс  -1 000 ₽");
    }

    @Test
    void browseCategory(@Amount(amount = -1000)
                        AccountTransaction transaction,
                        FakeSession session) {
        session.sendText("/expenses")
            .pressButton("Разобрать")
            .pressButton("Другое")
            .expectContains(transaction.getRaw())
            .expectContains("1000 ₽");
    }
}
