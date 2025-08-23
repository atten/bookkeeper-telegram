package bookkeeper.dao;

import bookkeeper.dao.entity.MerchantExpenditure;
import bookkeeper.dao.entity.TelegramUser;
import bookkeeper.enums.Expenditure;
import dagger.Reusable;
import jakarta.persistence.EntityManager;

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
        var sql = "SELECT * FROM merchant_expenditures WHERE merchant ILIKE :merchant AND telegram_user = :telegramUser ORDER BY rank DESC LIMIT 1";
        var query = manager.createNativeQuery(sql, MerchantExpenditure.class)
            .setParameter("merchant", '%' + normalizedMerchant + '%')
            .setParameter("telegramUser", user.getTelegramId());
        var result = query.getSingleResultOrNull();

        if (result != null) {
            return ((MerchantExpenditure)result).getExpenditure();
        } else {
            return Expenditure.OTHER;
        }
    }

    public void rememberExpenditurePreference(String merchant, Expenditure expenditure, TelegramUser user) {
        var normalizedMerchant = normalizeMerchant(merchant);
        var currentExpenditure = getPreferredExpenditureForMerchant(merchant, user);

        if (currentExpenditure == Expenditure.OTHER) {
            // save new
            var obj = newItemFactory(normalizedMerchant, expenditure, user);
            obj.setRank(1);
            manager.merge(obj);
        } else {
            // update existing
            var sql = "UPDATE merchant_expenditures SET rank = rank + 1 WHERE expenditure = :expenditure AND merchant = :merchant AND telegram_user = :telegramUser";
            var query = manager.createNativeQuery(sql)
                .setParameter("merchant", normalizedMerchant)
                .setParameter("expenditure", expenditure.ordinal())
                .setParameter("telegramUser", user.getTelegramId());

            query.executeUpdate();
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
