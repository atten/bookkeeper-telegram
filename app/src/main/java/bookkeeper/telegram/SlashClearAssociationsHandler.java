package bookkeeper.telegram;

import bookkeeper.repositories.MerchantExpenditureRepository;
import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.util.Objects;

import static bookkeeper.telegram.shared.TransactionResponseFactory.getTextPluralForm;

/**
 * Scenario: User clears merchant-expenditure associations.
 */
public class SlashClearAssociationsHandler extends AbstractHandler {
    private final MerchantExpenditureRepository merchantExpenditureRepository;

    public SlashClearAssociationsHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, MerchantExpenditureRepository merchantExpenditureRepository) {
        super(bot, telegramUserRepository);
        this.merchantExpenditureRepository = merchantExpenditureRepository;
    }

    @Override
    public Boolean handle(Update update) {
        if (update.message() == null || !Objects.equals(update.message().text(), "/clear_associations"))
            return false;

        var count = merchantExpenditureRepository.removeMerchantAssociations(getTelegramUser(update));
        var message = String.format(
            getTextPluralForm(
                count,
                "%s привязка категории очищена.",
                "%s привязки категорий очищены.",
                "%s привязок категорий очищено."
            ),
            count
        );

        sendMessage(update, message);
        return true;
    }
}
