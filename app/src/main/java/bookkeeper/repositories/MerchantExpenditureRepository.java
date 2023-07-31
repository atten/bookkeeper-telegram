package bookkeeper.repositories;

import bookkeeper.entities.MerchantExpenditure;
import bookkeeper.entities.TelegramUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.jetbrains.annotations.Nullable;

public class MerchantExpenditureRepository {
    private final EntityManager manager;

    public MerchantExpenditureRepository(EntityManager manager) {
        this.manager = manager;
    }

    @Nullable
    public MerchantExpenditure find(String merchant, TelegramUser telegramUser) {
        var sql = "SELECT i FROM MerchantExpenditure i WHERE i.merchant=:merchant AND i.telegramUser=:telegramUser ORDER BY i.createdAt DESC LIMIT 1";
        var query = manager.createQuery(sql, MerchantExpenditure.class)
            .setParameter("merchant", merchant)
            .setParameter("telegramUser", telegramUser);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
