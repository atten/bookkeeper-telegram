package bookkeeper.telegram.scenarios.refine;

import bookkeeper.enums.Expenditure;
import bookkeeper.repositories.MerchantExpenditureRepository;
import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseMessage;


public class MerchantExpenditureRemoveCallbackHandler extends AbstractHandler {
    private final MerchantExpenditureRepository merchantExpenditureRepository;

    public MerchantExpenditureRemoveCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, MerchantExpenditureRepository merchantExpenditureRepository) {
        super(bot, telegramUserRepository);
        this.merchantExpenditureRepository = merchantExpenditureRepository;
    }

    @Override
    public Boolean handle(Update update) {
        var callbackMessage = getCallbackMessage(update);
        if (!(callbackMessage instanceof MerchantExpenditureRemoveCallback))
            return false;

        var cm = ((MerchantExpenditureRemoveCallback) callbackMessage);
        merchantExpenditureRepository.removeMerchantAssociation(cm.getMerchant(), cm.getExpenditure(), getTelegramUser(update));
        editMessage(update, getResponseMessage(cm.getMerchant(), Expenditure.OTHER));
        return true;
    }
}
