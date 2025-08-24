package bookkeeper.dao.repository;

import bookkeeper.dao.entity.Account;
import bookkeeper.dao.entity.AccountTransfer;
import bookkeeper.dao.entity.TelegramUser;
import dagger.Reusable;
import jakarta.persistence.EntityManager;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

@Reusable
public class AccountTransferRepository {
    private final EntityManager manager;

    @Inject
    AccountTransferRepository(EntityManager manager) {
        this.manager = manager;
    }

    public AccountTransfer create(BigDecimal withdrawAmount, Account withdrawAccount, BigDecimal depositAmount, Account depositAccount, int monthOffset) {
        var transaction = transferFactory(withdrawAmount, withdrawAccount, depositAmount, depositAccount);
        transaction.setTimestamp(transaction.getTimestamp().plus(monthOffset * 30L, ChronoUnit.DAYS));
        manager.persist(transaction);
        return transaction;
    }

    public Map<Long, BigDecimal> getTransferBalances(TelegramUser user, int monthOffset) {
        var monthClause = "DateTime::MakeDate(DateTime::StartOfMonth(timestamp)) <= DateTime::MakeDate(DateTime::ShiftMonths(DateTime::StartOfMonth(CurrentUtcDate()), :monthOffset))";
        var sql =
                "SELECT account_id, SUM(amount) FROM " +
                "(" +
                "    SELECT t.deposit_account_id as account_id, SUM(deposit_amount) AS amount FROM account_transfers t JOIN accounts a ON a.id = t.deposit_account_id WHERE a.telegram_user = :user AND " + monthClause + " GROUP BY t.deposit_account_id " +
                "    UNION " +
                "    SELECT t.withdraw_account_id as account_id, SUM(withdraw_amount) AS amount FROM account_transfers t JOIN accounts a ON a.id = t.withdraw_account_id WHERE a.telegram_user = :user AND " + monthClause +" GROUP BY t.withdraw_account_id " +
                ") amounts GROUP BY account_id";

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

    private AccountTransfer transferFactory(BigDecimal withdrawAmount, Account withdrawAccount, BigDecimal depositAmount, Account depositAccount) {
        var transfer = new AccountTransfer();
        transfer.setWithdrawAmount(withdrawAmount);
        transfer.setWithdrawAccount(withdrawAccount);
        transfer.setDepositAmount(depositAmount);
        transfer.setDepositAccount(depositAccount);
        transfer.setCreatedAt(Instant.now());
        transfer.setTimestamp(Instant.now());
        return transfer;
    }

}
