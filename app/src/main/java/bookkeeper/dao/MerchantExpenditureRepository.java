package bookkeeper.dao;

import bookkeeper.dao.entity.MerchantExpenditure;
import bookkeeper.dao.entity.TelegramUser;
import bookkeeper.enums.Expenditure;
import dagger.Reusable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import javax.inject.Inject;
import java.time.Instant;

@Reusable
public class MerchantExpenditureRepository {
    private final EntityManager manager;

    @Inject
    MerchantExpenditureRepository(EntityManager manager) {
        this.manager = manager;
    }

    public Expenditure getPreferredExpenditureForMerchant(String merchant, TelegramUser user) {
        var normalizedMerchant = normalizeMerchant(merchant);
        var sql = "SELECT i FROM MerchantExpenditure i WHERE i.merchant ILIKE :merchant AND i.telegramUser=:telegramUser ORDER BY i.rank DESC LIMIT 1";
        var query = manager.createQuery(sql, MerchantExpenditure.class)
            .setParameter("merchant", '%' + normalizedMerchant + '%')
            .setParameter("telegramUser", user);

        try {
            return query.getSingleResult().getExpenditure();
        } catch (NoResultException e) {
            return Expenditure.OTHER;
        }
    }

    public void rememberExpenditurePreference(String merchant, Expenditure expenditure, TelegramUser user) {
        var normalizedMerchant = normalizeMerchant(merchant);
        var sql = "UPDATE MerchantExpenditure i SET rank = rank + 1 WHERE i.expenditure=:expenditure AND i.merchant=:merchant AND i.telegramUser=:telegramUser";
        var query = manager.createQuery(sql)
            .setParameter("merchant", normalizedMerchant)
            .setParameter("expenditure", expenditure)
            .setParameter("telegramUser", user);

        int count = query.executeUpdate();
        if (count == 0) {
            // no records updated
            var obj = newItemFactory(normalizedMerchant, expenditure, user);
            obj.setRank(1);
            manager.merge(obj);
        }
    }

    private String normalizeMerchant(String merchant) {
        var normalized = new StringBuilder();

        for (var c: merchant.toCharArray()) {
            if (Character.isAlphabetic(c))
                normalized.append(Character.toLowerCase(c));
        }

        if (normalized.isEmpty()) {
            // fallback to original merchant if normalization fails
            return merchant;
        }
        return normalized.toString();
    }

    private MerchantExpenditure newItemFactory(String merchant, Expenditure expenditure, TelegramUser user) {
        var obj = new MerchantExpenditure();
        obj.setRank(0);
        obj.setMerchant(merchant);
        obj.setExpenditure(expenditure);
        obj.setTelegramUser(user);
        obj.setCreatedAt(Instant.now());
        return obj;
    }
}
