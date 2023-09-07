package bookkeeper.services.repositories;

import bookkeeper.entities.Account;
import bookkeeper.entities.AccountTransfer;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.Instant;

public class AccountTransferRepository {
    private final EntityManager manager;

    public AccountTransferRepository(EntityManager manager) {
        this.manager = manager;
    }

    public AccountTransfer create(BigDecimal withdrawAmount, Account withdrawAccount, BigDecimal depositAmount, Account depositAccount) {
        var transaction = transferFactory(withdrawAmount, withdrawAccount, depositAmount, depositAccount);
        manager.persist(transaction);
        return transaction;
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
