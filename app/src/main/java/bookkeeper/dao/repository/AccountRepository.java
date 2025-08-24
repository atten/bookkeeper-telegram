package bookkeeper.dao.repository;

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

    @SuppressWarnings("unchecked")
    public List<Account> filter(TelegramUser user) {
        var sql = "SELECT * FROM accounts WHERE telegram_user = :user ORDER BY name";
        var query = manager.createNativeQuery(sql, Account.class).setParameter("user", user.getTelegramId());
        return (List<Account>)query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Account> filter(TelegramUser user, Currency currency) {
        var sql = "SELECT * FROM accounts WHERE telegram_user = :user AND currency = :currency ORDER BY name";
        var query = manager.createNativeQuery(sql, Account.class)
                .setParameter("user", user.getTelegramId())
                .setParameter("currency", currency.getCurrencyCode());
        return (List<Account>)query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Account> filter(String nameOrNotesContains, Currency currency, TelegramUser user) {
        var sql = "SELECT * FROM accounts WHERE (name ILIKE :text OR notes ILIKE :text) AND currency = :currency AND telegram_user = :telegramUser ORDER BY name";
        var query = manager.createNativeQuery(sql, Account.class)
            .setParameter("text", '%' + nameOrNotesContains + '%')
            .setParameter("currency", currency.getCurrencyCode())
            .setParameter("telegramUser", user.getTelegramId());
        return (List<Account>)query.getResultList();
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
