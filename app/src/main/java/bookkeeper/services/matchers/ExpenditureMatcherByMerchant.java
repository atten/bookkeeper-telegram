package bookkeeper.services.matchers;

import bookkeeper.entities.TelegramUser;
import bookkeeper.enums.Expenditure;
import bookkeeper.services.repositories.MerchantExpenditureRepository;
import bookkeeper.services.parsers.Spending;
import org.jetbrains.annotations.NotNull;

public class ExpenditureMatcherByMerchant implements ExpenditureMatcher {
    private final MerchantExpenditureRepository merchantExpenditureRepository;

    public ExpenditureMatcherByMerchant(MerchantExpenditureRepository merchantExpenditureRepository) {
        this.merchantExpenditureRepository = merchantExpenditureRepository;
    }

    @NotNull
    @Override
    public Expenditure match(Spending spending, TelegramUser telegramUser) {
        var resultFromRepo = merchantExpenditureRepository.find(spending.getMerchant(), telegramUser);
        if (resultFromRepo != null)
            return resultFromRepo.getExpenditure();

        return Expenditure.OTHER;
    }
}
