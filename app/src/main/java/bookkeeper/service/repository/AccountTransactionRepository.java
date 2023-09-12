package bookkeeper.service.repository;

import bookkeeper.entity.Account;
import bookkeeper.entity.AccountTransaction;
import bookkeeper.entity.TelegramUser;
import bookkeeper.enums.Expenditure;
import dagger.Reusable;
import jakarta.persistence.EntityManager;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Reusable
public class AccountTransactionRepository {
    private final EntityManager manager;

    @Inject
    public AccountTransactionRepository(EntityManager manager) {
        this.manager = manager;
    }

    public Optional<AccountTransaction> get(long transactionId) {
        return Optional.ofNullable(manager.find(AccountTransaction.class, transactionId));
    }

    public List<AccountTransaction> findByIds(List<Long> transactionIds) {
        var sql = "SELECT i FROM AccountTransaction i WHERE i.id IN :transactionIds";
        var query = manager.createQuery(sql, AccountTransaction.class).setParameter("transactionIds", transactionIds);
        return query.getResultList();
    }

    public List<AccountTransaction> findRecent(TelegramUser user, Currency currency, int count) {
        var sql = "SELECT i FROM AccountTransaction i WHERE account.telegramUser=:user AND account.currency=:currency ORDER BY createdAt DESC LIMIT :count";
        var query = manager.createQuery(sql, AccountTransaction.class)
            .setParameter("user", user)
            .setParameter("currency", currency.getCurrencyCode())
            .setParameter("count", count);
        return query.getResultList();
    }

    public List<AccountTransaction> findByCreatedAt(Instant createdAt, TelegramUser user) {
        var sql = "SELECT i FROM AccountTransaction i WHERE account.telegramUser=:user AND createdAt = :createdAt";
        var query = manager.createQuery(sql, AccountTransaction.class)
                .setParameter("createdAt", createdAt)
                .setParameter("user", user);
        return query.getResultList();
    }

    public List<Long> findIds(Expenditure expenditure, int monthOffset, TelegramUser user) {
        var sql = "SELECT id from AccountTransaction " +
                "WHERE account.telegramUser=:user " +
                "AND expenditure=:expenditure " +
                "AND date_trunc('month', timestamp) = date_trunc('month', current_timestamp) + :monthOffset MONTH";

        var query = manager.createQuery(sql, Long.class)
                .setParameter("user", user)
                .setParameter("expenditure", expenditure)
                .setParameter("monthOffset", monthOffset);

        return query.getResultList();
    }

    public Map<Expenditure, BigDecimal> getMonthlyAmount(Account account, int monthOffset) {
        var sql = "SELECT expenditure, SUM(amount) from AccountTransaction " +
                "WHERE account=:account " +
                "AND date_trunc('month', timestamp) = date_trunc('month', current_timestamp) + :monthOffset MONTH " +
                "GROUP BY expenditure";

        var query = manager.createQuery(sql)
            .setParameter("account", account)
            .setParameter("monthOffset", monthOffset);

        Map<Expenditure, BigDecimal> result = new LinkedHashMap<>();

        for (var entry : query.getResultList()) {
            var arrayEntry = (Object[]) entry;
            result.put((Expenditure) arrayEntry[0], (BigDecimal) arrayEntry[1]);
        }

        return result;
    }

    public BigDecimal getTransactionBalance(Account account, int monthOffset) {
        var sql = "SELECT SUM(amount) from AccountTransaction " +
                "WHERE account=:account " +
                "AND date_trunc('month', timestamp) <= date_trunc('month', current_timestamp) + :monthOffset MONTH";

        var query = manager.createQuery(sql)
                .setParameter("account", account)
                .setParameter("monthOffset", monthOffset);

        var result = query.getSingleResult();
        return result == null ? BigDecimal.ZERO : (BigDecimal) result;
    }

    public void approve(AccountTransaction transaction) {
        if (transaction.isApproved())
            return;
        transaction.setApprovedAt(Instant.now());
    }

    public void save(AccountTransaction transaction) {
        manager.persist(transaction);
    }

    public void remove(AccountTransaction transaction) {
        manager.remove(transaction);
    }
}
