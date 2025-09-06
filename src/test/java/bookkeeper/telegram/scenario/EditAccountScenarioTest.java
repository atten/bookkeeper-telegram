package bookkeeper.telegram.scenario;

import bookkeeper.dao.entity.Account;
import bookkeeper.resolverAnnotations.Hidden;
import bookkeeper.resolverAnnotations.Name;
import bookkeeper.telegram.BookkeeperParameterResolver;
import bookkeeper.telegram.FakeSession;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BookkeeperParameterResolver.class)
class EditAccountScenarioTest {
    @Test
    void changeAccountName(
        @Name(name = "Lupa")
        Account account,
        FakeSession session,
        EntityManager manager
    ) {
        session
            .sendText("/accounts")
            .pressButton("Lupa")
            .pressButton("Переименовать")
            .reply("Pupa");

        var acc = manager.find(Account.class, account.getId());
        Assertions.assertEquals(acc.getName(), "Pupa");
    }

    @Test
    void changeAccountNotes(
        @Name(name = "Account1")
        Account account,
        FakeSession session,
        EntityManager manager
    ) {
        session
            .sendText("/accounts")
            .pressButton("Account1")
            .pressButton("Заметки")
            .reply("Это заметка для счёта");

        var acc = manager.find(Account.class, account.getId());
        Assertions.assertEquals(acc.getNotes(), "Это заметка для счёта");
    }

    @Test
    void hideAccount(
        @Name(name = "Lupa")
        Account account,
        FakeSession session,
        EntityManager manager
    ) {
        session
            .sendText("/accounts")
            .pressButton("Lupa")
            .pressButton("Скрыть");

        var acc = manager.find(Account.class, account.getId());
        Assertions.assertTrue(acc.isHidden());
    }

    @Test
    void showAccount(
        @Name(name = "Lupa")
        @Hidden
        Account account,
        FakeSession session,
        EntityManager manager
    ) {
        session
            .sendText("/accounts")
            .pressButton("Скрытые")
            .pressButton("Lupa")
            .pressButton("Показать");

        var acc = manager.find(Account.class, account.getId());
        Assertions.assertTrue(acc.isVisible());
    }
}
