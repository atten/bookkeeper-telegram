package bookkeeper.repositories;

import bookkeeper.entities.AccountTransaction;
import jakarta.persistence.EntityManager;

public class AccountTransactionRepository {
    private final EntityManager manager;

    public AccountTransactionRepository(EntityManager manager) {
        this.manager = manager;
    }

    public void save(AccountTransaction transaction) {
        manager.persist(transaction);
    }
}
