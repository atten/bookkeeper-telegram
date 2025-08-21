package bookkeeper.dao;

import bookkeeper.dao.entity.Account;
import bookkeeper.dao.entity.AccountTransfer;
import dagger.Reusable;
import jakarta.persistence.EntityManager;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

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

    public BigDecimal getTransferBalance(Account account, int monthOffset) {
        var monthClause = "DateTime::MakeDate(DateTime::StartOfMonth(timestamp)) <= DateTime::MakeDate(DateTime::ShiftMonths(DateTime::StartOfMonth(CurrentUtcDate()), :monthOffset))";
        var sql =
                "SELECT SUM(amount) FROM " +
                "(" +
                "    SELECT SUM(deposit_amount) AS amount FROM account_transfers WHERE deposit_account_id = :account AND " + monthClause +
                "    UNION " +
                "    SELECT SUM(withdraw_amount) AS amount FROM account_transfers WHERE withdraw_account_id = :account AND " + monthClause +
                ") amounts";

        var query = manager.createNativeQuery(sql)
                .setParameter("account", account.getId())
                .setParameter("monthOffset", monthOffset);

        var result = query.getSingleResult();
        return result == null ? BigDecimal.ZERO : (BigDecimal) result;
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
