package bookkeeper.services.repositories;

import bookkeeper.entities.Account;
import bookkeeper.entities.TelegramUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.time.Instant;
import java.util.Currency;
import java.util.List;

public class AccountRepository {
    private final EntityManager manager;

    public AccountRepository(EntityManager manager) {
        this.manager = manager;
    }

    public Account getOrCreate(String name, Currency currency, TelegramUser user) {
        var sql = "SELECT i FROM Account i WHERE i.name=:name AND i.currency=:currency AND i.telegramUser=:telegramUser";
        var query = manager.createQuery(sql, Account.class)
                .setParameter("name", name)
                .setParameter("currency", currency.getCurrencyCode())
                .setParameter("telegramUser", user);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return manager.merge(newAccountFactory(name, currency, user));
        }
    }

    public List<Account> findForUser(TelegramUser user) {
        var sql = "SELECT i FROM Account i WHERE i.telegramUser = :user";
        var query = manager.createQuery(sql, Account.class).setParameter("user", user);
        return query.getResultList();
    }

    private Account newAccountFactory(String name, Currency currency, TelegramUser user) {
        var account = new Account();
        account.setName(name);
        account.setCurrency(currency);
        account.setTelegramUser(user);
        account.setCreatedAt(Instant.now());
        return account;
    }

}
