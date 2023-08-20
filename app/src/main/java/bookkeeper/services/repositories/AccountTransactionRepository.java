package bookkeeper.services.repositories;

import bookkeeper.entities.Account;
import bookkeeper.entities.AccountTransaction;
import bookkeeper.entities.TelegramUser;
import bookkeeper.enums.Expenditure;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class AccountTransactionRepository {
    private final EntityManager manager;

    public AccountTransactionRepository(EntityManager manager) {
        this.manager = manager;
    }

    @Nullable
    public AccountTransaction get(long transactionId) {
        return manager.find(AccountTransaction.class, transactionId);
    }

    public List<AccountTransaction> getByIds(List<Long> transactionIds) {
        var sql = "SELECT i FROM AccountTransaction i WHERE i.id IN :transactionIds";
        var query = manager.createQuery(sql, AccountTransaction.class).setParameter("transactionIds", transactionIds);
        return query.getResultList();
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

    public BigDecimal getMonthlyAmount(Account account, Expenditure expenditure, int monthDelta) {
        var sql = "SELECT SUM(amount) from AccountTransaction " +
                "WHERE account=:account " +
                "AND expenditure=:expenditure " +
                "AND date_trunc('month', timestamp) = date_trunc('month', current_timestamp) - :monthDelta MONTH";

        var query = manager.createQuery(sql)
            .setParameter("account", account)
            .setParameter("expenditure", expenditure)
            .setParameter("monthDelta", monthDelta);

        var result = query.getSingleResult();

        if (result == null)
            return BigDecimal.ZERO;

        return (BigDecimal) result;
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
