package bookkeeper.dao;

import bookkeeper.dao.entity.Account;
import bookkeeper.dao.entity.AccountTransaction;
import bookkeeper.dao.entity.TelegramUser;
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

    @SuppressWarnings("unchecked")
    public List<AccountTransaction> findByIds(List<Long> transactionIds) {
        var sql = "SELECT * FROM account_transactions WHERE id IN :transactionIds";
        var query = manager.createNativeQuery(sql, AccountTransaction.class).setParameter("transactionIds", transactionIds);
        return (List<AccountTransaction>)query.getResultList();
    }

    public List<AccountTransaction> findRecentAdded(Account account, int count) {
        return findRecentAdded(Set.of(account), count);
    }

    @SuppressWarnings("unchecked")
    public List<AccountTransaction> findRecentAdded(Collection<Account> accounts, int count) {
        var sql = "SELECT * FROM account_transactions i WHERE account_id IN :accounts ORDER BY id DESC LIMIT :count";
        var query = manager.createNativeQuery(sql, AccountTransaction.class)
            .setParameter("accounts", accounts.stream().map(Account::getId).toList())
            .setParameter("count", count);

        return (List<AccountTransaction>)query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Long> findIds(Expenditure expenditure, int monthOffset, TelegramUser user) {
        var sql = "SELECT t.id FROM account_transactions t " +
                "JOIN accounts a ON a.id = t.account_id " +
                "WHERE a.telegram_user = :user " +
                "AND expenditure = :expenditure " +
                "AND DateTime::MakeDate(DateTime::StartOfMonth(t.timestamp)) = DateTime::MakeDate(DateTime::ShiftMonths(DateTime::StartOfMonth(CurrentUtcDate()), :monthOffset))";

        var query = manager.createNativeQuery(sql)
                .setParameter("user", user.getTelegramId())
                .setParameter("expenditure", expenditure.ordinal())
                .setParameter("monthOffset", monthOffset);

        return (List<Long>)query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<AccountTransaction> findByRawText(String text, int monthOffset, TelegramUser user) {
        var sql = "SELECT t.* FROM account_transactions t " +
            "JOIN accounts a ON a.id = t.account_id " +
            "WHERE a.telegram_user = :user " +
            "AND t.raw ILIKE :text " +
            "AND DateTime::MakeDate(DateTime::StartOfMonth(t.timestamp)) = DateTime::MakeDate(DateTime::ShiftMonths(DateTime::StartOfMonth(CurrentUtcDate()), :monthOffset)) " +
            "ORDER BY timestamp ASC";

        var query = manager.createNativeQuery(sql, AccountTransaction.class)
            .setParameter("user", user.getTelegramId())
            .setParameter("text", '%' + text + '%')
            .setParameter("monthOffset", monthOffset);

        return (List<AccountTransaction>)query.getResultList();
    }

    public List<AccountTransaction> findByExpenditureName(String text, int monthOffset, TelegramUser user) {
        var expenditure = Expenditure
            .enabledValues()
            .stream()
            .filter(e -> e.getVerboseName().toLowerCase().contains(text.toLowerCase()))
            .findFirst();

        if (expenditure.isEmpty()) {
            return List.of();
        }

        var ids = findIds(expenditure.get(), monthOffset, user);
        return findByIds(ids);
    }

    public Map<Expenditure, BigDecimal> getMonthlyAmount(Account account, int monthOffset) {
        var sql = "SELECT t.expenditure, SUM(amount) FROM account_transactions t " +
                "JOIN accounts a ON a.id = t.account_id " +
                "WHERE a.id = :account " +
                "AND DateTime::MakeDate(DateTime::StartOfMonth(t.timestamp)) = DateTime::MakeDate(DateTime::ShiftMonths(DateTime::StartOfMonth(CurrentUtcDate()), :monthOffset)) " +
                "GROUP BY t.expenditure";

        var query = manager.createNativeQuery(sql)
            .setParameter("account", account.getId())
            .setParameter("monthOffset", monthOffset);

        Map<Expenditure, BigDecimal> result = new LinkedHashMap<>();

        for (var entry : query.getResultList()) {
            var arrayEntry = (Object[]) entry;
            result.put(Expenditure.values()[(Short)(arrayEntry[0])], (BigDecimal) arrayEntry[1]);
        }

        return result;
    }

    public BigDecimal getTransactionBalance(Account account, int monthOffset) {
        var sql = "SELECT SUM(amount) FROM account_transactions " +
                "WHERE account_id = :account " +
                "AND DateTime::MakeDate(DateTime::StartOfMonth(timestamp)) <= DateTime::MakeDate(DateTime::ShiftMonths(DateTime::StartOfMonth(CurrentUtcDate()), :monthOffset))";

        var query = manager.createNativeQuery(sql)
                .setParameter("account", account.getId())
                .setParameter("monthOffset", monthOffset);

        var result = query.getSingleResult();
        return result == null ? BigDecimal.ZERO : (BigDecimal) result;
    }

    public void approve(AccountTransaction transaction) {
        if (transaction.isApproved())
            return;
        transaction.setApprovedAt(Instant.now());
    }

    public void unapprove(AccountTransaction transaction) {
        transaction.setApprovedAt(null);
    }

    public void save(AccountTransaction transaction) {
        manager.persist(transaction);
    }

    public void remove(AccountTransaction transaction) {
        manager.remove(transaction);
    }
}
