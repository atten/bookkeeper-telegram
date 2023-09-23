package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.service.repository.MerchantExpenditureRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.Request;

import javax.inject.Inject;
import java.util.Objects;

import static bookkeeper.telegram.shared.StringUtils.pluralizeTemplate;

/**
 * Scenario: User clears merchant-expenditure associations.
 */
class SlashClearAssociationsHandler implements AbstractHandler {
    private final MerchantExpenditureRepository merchantExpenditureRepository;

    @Inject
    SlashClearAssociationsHandler(MerchantExpenditureRepository merchantExpenditureRepository) {
        this.merchantExpenditureRepository = merchantExpenditureRepository;
    }

    public Boolean handle(Request request) {
        if (!Objects.equals(request.getMessageText(), "/clear_associations"))
            return false;

        var count = merchantExpenditureRepository.removeMerchantAssociations(request.getTelegramUser());
        var message = pluralizeTemplate(
            count,
            "%s привязка категории очищена.",
            "%s привязки категорий очищены.",
            "%s привязок категорий очищено."
        );

        request.sendMessage(message);
        return true;
    }
}
