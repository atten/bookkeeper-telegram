package bookkeeper.services.matchers;

import bookkeeper.entities.MerchantExpenditure;
import bookkeeper.entities.TelegramUser;
import bookkeeper.enums.Expenditure;
import bookkeeper.services.repositories.MerchantExpenditureRepository;
import bookkeeper.services.parsers.Spending;

public class ExpenditureMatcherByMerchant implements ExpenditureMatcher {
    private final MerchantExpenditureRepository merchantExpenditureRepository;

    public ExpenditureMatcherByMerchant(MerchantExpenditureRepository merchantExpenditureRepository) {
        this.merchantExpenditureRepository = merchantExpenditureRepository;
    }

    @Override
    public Expenditure match(Spending spending, TelegramUser telegramUser) {
        return merchantExpenditureRepository.find(spending.getMerchant(), telegramUser)
                .map(MerchantExpenditure::getExpenditure)
                .orElse(Expenditure.OTHER);
    }
}
