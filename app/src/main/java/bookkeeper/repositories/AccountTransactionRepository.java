package bookkeeper.repositories;

import bookkeeper.entities.AccountTransaction;
import bookkeeper.enums.Expenditure;
import jakarta.persistence.EntityManager;

import java.time.Instant;

public class AccountTransactionRepository {
    private final EntityManager manager;

    public AccountTransactionRepository(EntityManager manager) {
        this.manager = manager;
    }

    public AccountTransaction get(long transactionId) {
        return manager.find(AccountTransaction.class, transactionId);
    }

    public void associateExpenditure(AccountTransaction transaction, Expenditure expenditure) {
        transaction.setExpenditure(expenditure);
        transaction.setApprovedAt(Instant.now());
    }

    public void save(AccountTransaction transaction) {
        manager.persist(transaction);
    }
}
