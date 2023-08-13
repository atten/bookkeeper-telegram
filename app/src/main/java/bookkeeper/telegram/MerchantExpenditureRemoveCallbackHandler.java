package bookkeeper.telegram;

import bookkeeper.enums.Expenditure;
import bookkeeper.repositories.MerchantExpenditureRepository;
import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.telegram.callbacks.MerchantExpenditureRemoveCallback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;


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
        var message = String.format("Категория *%s* будет использоваться по умолчанию для последующих записей `%s`.", Expenditure.OTHER.getName(), cm.getMerchant());
        editMessage(update, message);
        return true;
    }
}
