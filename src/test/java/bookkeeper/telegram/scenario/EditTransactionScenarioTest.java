package bookkeeper.telegram.scenario;

import bookkeeper.dao.entity.Account;
import bookkeeper.dao.entity.AccountTransaction;
import bookkeeper.enums.Expenditure;
import bookkeeper.resolverAnnotations.Name;
import bookkeeper.telegram.BookkeeperParameterResolver;
import bookkeeper.telegram.FakeSession;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BookkeeperParameterResolver.class)
class EditTransactionScenarioTest {
    @Test
    void approveTransaction(AccountTransaction transaction, FakeSession session, EntityManager manager) {
        session
            .sendText("/expenses")
            .pressButton("Разобрать")
            .pressButton("Другое")
            .pressButton("Подтвердить");

        var tx = manager.find(AccountTransaction.class, transaction.getId());
        Assertions.assertTrue(tx.isApproved());
    }

    @Test
    void changeTransactionExpenditure(AccountTransaction transaction, FakeSession session, EntityManager manager) {
        session
            .sendText("/expenses")
            .pressButton("Разобрать")
            .pressButton("Другое")
            .pressButton("Категория")
            .pressButton("Транспорт");

        var tx = manager.find(AccountTransaction.class, transaction.getId());
        Assertions.assertEquals(tx.getExpenditure(), Expenditure.TRANSPORT);
    }

    @Test
    void changeTransactionAccount(
        @Name(name = "Buzinga")
        Account otherAccount,
        AccountTransaction transaction, FakeSession session, EntityManager manager
    ) {
        session
            .sendText("/expenses")
            .pressButton("Разобрать")
            .pressButton("Другое")
            .pressButton("Счёт")
            .pressButton("Buzinga");

        var tx = manager.find(AccountTransaction.class, transaction.getId());
        Assertions.assertEquals(tx.getAccount().getId(), otherAccount.getId());
    }

    @Test
    void removeTransaction(FakeSession session) {
        session
            .sendText("еда 20")
            .pressButton("Отмена")
            .sendText("/expenses")
            .expectContains("Баланс  0");
    }
}
