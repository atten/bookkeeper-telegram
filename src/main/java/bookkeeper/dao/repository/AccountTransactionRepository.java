package bookkeeper.dao.repository;

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

    public Map<Expenditure, List<Long>> findIds(int monthOffset, TelegramUser user) {
        var sql = "SELECT t.id, t.expenditure FROM account_transactions t " +
                "JOIN accounts a ON a.id = t.account_id " +
                "WHERE a.telegram_user = :user " +
                "AND DateTime::MakeDate(DateTime::StartOfMonth(t.timestamp)) = DateTime::MakeDate(DateTime::ShiftMonths(DateTime::StartOfMonth(CurrentUtcDate()), :monthOffset))";

        var query = manager.createNativeQuery(sql)
                .setParameter("user", user.getTelegramId())
                .setParameter("monthOffset", monthOffset);

        Map<Expenditure, List<Long>> result = new LinkedHashMap<>();

        for (var entry : query.getResultList()) {
            var arrayEntry = (Object[]) entry;
            var id = (Long)arrayEntry[0];
            var expenditure = Expenditure.values()[(Short)(arrayEntry[1])];
            result.putIfAbsent(expenditure, new LinkedList<>());
            result.get(expenditure).add(id);
        }

        return result;
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

        var ids = findIds(monthOffset, user).getOrDefault(expenditure.get(), List.of());
        return findByIds(ids);
    }

    public Map<Long, Map<Expenditure, BigDecimal>> getMonthlyAmount(TelegramUser user, int monthOffset) {
        var sql = "SELECT t.account_id, t.expenditure, SUM(amount) FROM account_transactions t " +
                "JOIN accounts a ON a.id = t.account_id " +
                "WHERE a.telegram_user = :user " +
                "AND DateTime::MakeDate(DateTime::StartOfMonth(t.timestamp)) = DateTime::MakeDate(DateTime::ShiftMonths(DateTime::StartOfMonth(CurrentUtcDate()), :monthOffset)) " +
                "GROUP BY t.account_id, t.expenditure";

        var query = manager.createNativeQuery(sql)
            .setParameter("user", user.getTelegramId())
            .setParameter("monthOffset", monthOffset);


        Map<Long, Map<Expenditure, BigDecimal>> result = new LinkedHashMap<>();

        for (var entry : query.getResultList()) {
            var arrayEntry = (Object[]) entry;
            var accountId = (Long)arrayEntry[0];
            var expenditure = Expenditure.values()[(Short)(arrayEntry[1])];
            var amount = (BigDecimal) arrayEntry[2];
            result.putIfAbsent(accountId, new LinkedHashMap<>());
            result.get(accountId).put(expenditure, amount);
        }

        return result;
    }

    public Map<Long, BigDecimal> getTransactionBalances(TelegramUser user, int monthOffset) {
        var sql = "SELECT t.account_id, SUM(amount) FROM account_transactions t " +
                "JOIN accounts a ON a.id = t.account_id " +
                "WHERE a.telegram_user = :user " +
                "AND DateTime::MakeDate(DateTime::StartOfMonth(t.timestamp)) <= DateTime::MakeDate(DateTime::ShiftMonths(DateTime::StartOfMonth(CurrentUtcDate()), :monthOffset)) " +
                "GROUP BY t.account_id";

        var query = manager.createNativeQuery(sql)
                .setParameter("user", user.getTelegramId())
                .setParameter("monthOffset", monthOffset);

        Map<Long, BigDecimal> result = new LinkedHashMap<>();

        for (var entry : query.getResultList()) {
            var arrayEntry = (Object[]) entry;
            var accountId = (Long)arrayEntry[0];
            var amount = (BigDecimal) arrayEntry[1];
            result.put(accountId, amount);
        }

        return result;
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
