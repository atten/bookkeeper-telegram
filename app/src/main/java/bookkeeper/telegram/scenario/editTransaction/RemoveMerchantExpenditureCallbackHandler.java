package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.enums.Expenditure;
import bookkeeper.service.repository.MerchantExpenditureRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.Request;

import javax.inject.Inject;

import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseMessage;


class RemoveMerchantExpenditureCallbackHandler implements AbstractHandler {
    private final MerchantExpenditureRepository merchantExpenditureRepository;

    @Inject
    RemoveMerchantExpenditureCallbackHandler(MerchantExpenditureRepository merchantExpenditureRepository) {
        this.merchantExpenditureRepository = merchantExpenditureRepository;
    }

    public Boolean handle(Request request) {
        var callbackMessage = request.getCallbackMessage();
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof RemoveMerchantExpenditureCallback cm))
            return false;

        merchantExpenditureRepository.removeMerchantAssociation(cm.getMerchant(), cm.getExpenditure(), request.getTelegramUser());
        request.editMessage(getResponseMessage(cm.getMerchant(), Expenditure.OTHER));
        return true;
    }
}
