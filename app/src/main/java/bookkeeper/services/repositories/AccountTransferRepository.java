package bookkeeper.services.repositories;

import bookkeeper.entities.Account;
import bookkeeper.entities.AccountTransfer;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class AccountTransferRepository {
    private final EntityManager manager;

    public AccountTransferRepository(EntityManager manager) {
        this.manager = manager;
    }

    public AccountTransfer create(BigDecimal withdrawAmount, Account withdrawAccount, BigDecimal depositAmount, Account depositAccount, int monthOffset) {
        var transaction = transferFactory(withdrawAmount, withdrawAccount, depositAmount, depositAccount);
        transaction.setTimestamp(transaction.getTimestamp().plus(monthOffset * 30L, ChronoUnit.DAYS));
        manager.persist(transaction);
        return transaction;
    }

    public BigDecimal getTransferBalance(Account account, int monthOffset) {
        var monthClause = "date_trunc('month', timestamp) <= date_trunc('month', current_timestamp) + :monthOffset MONTH";
        var sql =
                "SELECT SUM(amount) FROM " +
                "(" +
                "    SELECT SUM(depositAmount) AS amount FROM AccountTransfer WHERE depositAccount = :account AND " + monthClause +
                "    UNION " +
                "    SELECT SUM(withdrawAmount) AS amount FROM AccountTransfer WHERE withdrawAccount = :account AND " + monthClause +
                ") amounts";

        var query = manager.createQuery(sql)
                .setParameter("account", account)
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
