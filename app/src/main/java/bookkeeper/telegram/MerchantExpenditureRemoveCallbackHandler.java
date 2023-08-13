package bookkeeper.telegram;

import bookkeeper.enums.Expenditure;
import bookkeeper.repositories.MerchantExpenditureRepository;
import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.telegram.callbacks.MerchantExpenditureRemoveCallback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import static bookkeeper.telegram.responses.TransactionResponseFactory.getResponseMessage;


public class MerchantExpenditureRemoveCallbackHandler extends AbstractHandler {
    private final MerchantExpenditureRepository merchantExpenditureRepository;

    MerchantExpenditureRemoveCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, MerchantExpenditureRepository merchantExpenditureRepository) {
        super(bot, telegramUserRepository);
        this.merchantExpenditureRepository = merchantExpenditureRepository;
    }

    @Override
    Boolean handle(Update update) {
        var callbackMessage = getCallbackMessage(update);
        if (!(callbackMessage instanceof MerchantExpenditureRemoveCallback))
            return false;

        var cm = ((MerchantExpenditureRemoveCallback) callbackMessage);
        merchantExpenditureRepository.removeMerchantAssociation(cm.getMerchant(), cm.getExpenditure(), getTelegramUser(update));
        editMessage(update, getResponseMessage(cm.getMerchant(), Expenditure.OTHER));
        return true;
    }
}
