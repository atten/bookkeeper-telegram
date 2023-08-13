package bookkeeper.repositories;

import bookkeeper.entities.MerchantExpenditure;
import bookkeeper.entities.TelegramUser;
import bookkeeper.enums.Expenditure;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

public class MerchantExpenditureRepository {
    private final EntityManager manager;

    public MerchantExpenditureRepository(EntityManager manager) {
        this.manager = manager;
    }

    @Nullable
    public MerchantExpenditure find(String merchant, TelegramUser user) {
        var sql = "SELECT i FROM MerchantExpenditure i WHERE i.merchant=:merchant AND i.telegramUser=:telegramUser ORDER BY i.createdAt DESC LIMIT 1";
        var query = manager.createQuery(sql, MerchantExpenditure.class)
            .setParameter("merchant", merchant)
            .setParameter("telegramUser", user);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public MerchantExpenditure addMerchantAssociation(String merchant, Expenditure expenditure, TelegramUser user) {
        var obj = newItemFactory(merchant, expenditure, user);
        return manager.merge(obj);
    }

    public void removeMerchantAssociation(String merchant, Expenditure expenditure, TelegramUser user) {
        var obj = find(merchant, user);
        if (obj == null || obj.getExpenditure() != expenditure)
            return;
        manager.remove(obj);
    }

    private MerchantExpenditure newItemFactory(String merchant, Expenditure expenditure, TelegramUser user) {
        var obj = new MerchantExpenditure();
        obj.setMerchant(merchant);
        obj.setExpenditure(expenditure);
        obj.setTelegramUser(user);
        obj.setCreatedAt(Instant.now());
        return obj;
    }
}
