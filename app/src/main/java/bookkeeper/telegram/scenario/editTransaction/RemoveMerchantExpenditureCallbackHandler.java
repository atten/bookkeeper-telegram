package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.enums.Expenditure;
import bookkeeper.service.repository.MerchantExpenditureRepository;
import bookkeeper.service.repository.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.service.registry.CallbackMessageRegistry;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import javax.inject.Inject;

import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseMessage;


class RemoveMerchantExpenditureCallbackHandler extends AbstractHandler {
    private final MerchantExpenditureRepository merchantExpenditureRepository;

    @Inject
    RemoveMerchantExpenditureCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, MerchantExpenditureRepository merchantExpenditureRepository) {
        super(bot, telegramUserRepository);
        this.merchantExpenditureRepository = merchantExpenditureRepository;
    }

    @Override
    public Boolean handle(Update update) {
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(update);
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof RemoveMerchantExpenditureCallback cm))
            return false;

        merchantExpenditureRepository.removeMerchantAssociation(cm.getMerchant(), cm.getExpenditure(), getTelegramUser(update));
        editMessage(update, getResponseMessage(cm.getMerchant(), Expenditure.OTHER));
        return true;
    }
}
