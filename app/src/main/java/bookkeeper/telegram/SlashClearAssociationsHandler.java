package bookkeeper.telegram;

import bookkeeper.services.repositories.MerchantExpenditureRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.util.Objects;

import static bookkeeper.telegram.shared.TransactionResponseFactory.pluralizeTemplate;

/**
 * Scenario: User clears merchant-expenditure associations.
 */
class SlashClearAssociationsHandler extends AbstractHandler {
    private final MerchantExpenditureRepository merchantExpenditureRepository;

    SlashClearAssociationsHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, MerchantExpenditureRepository merchantExpenditureRepository) {
        super(bot, telegramUserRepository);
        this.merchantExpenditureRepository = merchantExpenditureRepository;
    }

    @Override
    public Boolean handle(Update update) {
        if (!Objects.equals(getMessageText(update), "/clear_associations"))
            return false;

        var count = merchantExpenditureRepository.removeMerchantAssociations(getTelegramUser(update));
        var message = pluralizeTemplate(
            count,
            "%s привязка категории очищена.",
            "%s привязки категорий очищены.",
            "%s привязок категорий очищено."
        );

        sendMessage(update, message);
        return true;
    }
}
