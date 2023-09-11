package bookkeeper.service.matcher;

import bookkeeper.entity.MerchantExpenditure;
import bookkeeper.entity.TelegramUser;
import bookkeeper.enums.Expenditure;
import bookkeeper.service.repository.MerchantExpenditureRepository;
import bookkeeper.service.parser.Spending;

import javax.inject.Inject;

public class ExpenditureMatcherByMerchant implements ExpenditureMatcher {
    private final MerchantExpenditureRepository merchantExpenditureRepository;

    @Inject
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
