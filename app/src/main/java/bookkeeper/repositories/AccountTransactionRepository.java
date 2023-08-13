package bookkeeper.repositories;

import bookkeeper.entities.AccountTransaction;
import bookkeeper.entities.TelegramUser;
import bookkeeper.enums.Expenditure;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Map;
import java.util.stream.Collectors;

public class AccountTransactionRepository {
    private final EntityManager manager;

    public AccountTransactionRepository(EntityManager manager) {
        this.manager = manager;
    }

    public AccountTransaction get(long transactionId) {
        return manager.find(AccountTransaction.class, transactionId);
    }

    @Nullable
    public AccountTransaction findUnapproved(TelegramUser user) {
        var sql = "SELECT i FROM AccountTransaction i WHERE i.approvedAt=null AND i.account.telegramUser=:telegramUser ORDER BY i.timestamp DESC LIMIT 1";
        var query = manager.createQuery(sql, AccountTransaction.class)
                .setParameter("telegramUser", user);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Map<Currency, BigDecimal> getTotalExpenses(Instant start, Instant end, TelegramUser user) {
        var sql = "SELECT account, SUM(amount) FROM AccountTransaction i WHERE i.telegramUser=:telegramUser GROUP BY i.account";
        var query = manager.createQuery(sql)
                .setParameter("telegramUser", user);

        query.getResultStream().collect(Collectors.toList());

        Map<Currency, BigDecimal> result = Map.of();
        return result;
    }

    public void approve(AccountTransaction transaction) {
        transaction.setApprovedAt(Instant.now());
    }

    public void associateExpenditure(AccountTransaction transaction, Expenditure expenditure) {
        transaction.setExpenditure(expenditure);
        approve(transaction);
    }

    public void save(AccountTransaction transaction) {
        manager.persist(transaction);
    }
}
