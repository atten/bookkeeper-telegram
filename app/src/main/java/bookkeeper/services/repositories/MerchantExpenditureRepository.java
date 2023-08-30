package bookkeeper.services.repositories;

import bookkeeper.entities.MerchantExpenditure;
import bookkeeper.entities.TelegramUser;
import bookkeeper.enums.Expenditure;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.time.Instant;
import java.util.Optional;

public class MerchantExpenditureRepository {
    private final EntityManager manager;

    public MerchantExpenditureRepository(EntityManager manager) {
        this.manager = manager;
    }

    public Optional<MerchantExpenditure> find(String merchant, TelegramUser user) {
        var sql = "SELECT i FROM MerchantExpenditure i WHERE i.merchant=:merchant AND i.telegramUser=:telegramUser ORDER BY i.createdAt DESC LIMIT 1";
        var query = manager.createQuery(sql, MerchantExpenditure.class)
            .setParameter("merchant", merchant)
            .setParameter("telegramUser", user);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public void addMerchantAssociation(String merchant, Expenditure expenditure, TelegramUser user) {
        var obj = newItemFactory(merchant, expenditure, user);
        manager.merge(obj);
    }

    public void removeMerchantAssociation(String merchant, Expenditure expenditure, TelegramUser user) {
        find(merchant, user).ifPresent(obj -> {
            if (obj.getExpenditure() != expenditure)
                manager.remove(obj);
        });
    }

    public int removeMerchantAssociations(TelegramUser user) {
        var sql = "DELETE FROM MerchantExpenditure i WHERE i.telegramUser=:telegramUser";
        var query = manager.createQuery(sql)
                .setParameter("telegramUser", user);
        return query.executeUpdate();
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
