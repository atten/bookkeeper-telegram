package bookkeeper.dao;

import bookkeeper.dao.entity.Account;
import bookkeeper.dao.entity.TelegramUser;
import dagger.Reusable;
import jakarta.persistence.EntityManager;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Reusable
public class AccountRepository {
    private final EntityManager manager;

    @Inject
    public AccountRepository(EntityManager manager) {
        this.manager = manager;
    }

    /**
     * Find account with the closest name match or create a new one.
     */
    public Account getMatchOrCreate(String name, Currency currency, TelegramUser user) {
        var candidates = filter(name, currency, user)
            .stream()
            .sorted(Comparator.comparingInt(account -> account.getName().length()));

        return candidates
            .findFirst()
            .orElseGet(() -> manager.merge(newAccountFactory(name, currency, user)));
    }

    public Optional<Account> get(long id) {
        return Optional.ofNullable(manager.find(Account.class, id));
    }

    public List<Account> filter(TelegramUser user) {
        var sql = "SELECT i FROM Account i WHERE i.telegramUser = :user ORDER BY i.name";
        var query = manager.createQuery(sql, Account.class).setParameter("user", user);
        return query.getResultList();
    }

    public List<Account> filter(TelegramUser user, Currency currency) {
        var sql = "SELECT i FROM Account i WHERE i.telegramUser = :user AND i.currency = :currency ORDER BY i.name";
        var query = manager.createQuery(sql, Account.class)
                .setParameter("user", user)
                .setParameter("currency", currency.getCurrencyCode());
        return query.getResultList();
    }

    public List<Account> filter(String nameOrNotesContains, Currency currency, TelegramUser user) {
        var sql = "SELECT i FROM Account i WHERE (i.name ILIKE :text OR i.notes ILIKE :text) AND i.currency=:currency AND i.telegramUser=:telegramUser ORDER BY i.name";
        var query = manager.createQuery(sql, Account.class)
            .setParameter("text", '%' + nameOrNotesContains + '%')
            .setParameter("currency", currency.getCurrencyCode())
            .setParameter("telegramUser", user);
        return query.getResultList();
    }

    private Account newAccountFactory(String name, Currency currency, TelegramUser user) {
        var account = new Account();
        account.setName(name);
        account.setCurrency(currency);
        account.setTelegramUser(user);
        account.setCreatedAt(Instant.now());
        account.setHidden(false);
        return account;
    }

}
