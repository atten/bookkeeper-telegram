package bookkeeper.telegram.scenario.viewAssets;

import bookkeeper.service.repository.AccountRepository;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.service.repository.AccountTransferRepository;
import bookkeeper.service.repository.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.CallbackMessageRegistry;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Objects;


/**
 * Scenario: user requests total assets.
 */
class ViewAssetsHandler extends AbstractHandler {
    private final AssetsResponseFactory assetsResponseFactory;

    @Inject
    ViewAssetsHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountRepository accountRepository, AccountTransactionRepository transactionRepository, AccountTransferRepository transferRepository) {
        super(bot, telegramUserRepository);
        this.assetsResponseFactory = new AssetsResponseFactory(accountRepository, transactionRepository, transferRepository);
    }

    /**
     * Display total assets overview
     */
    @Override
    public Boolean handle(Update update) {
        return handleCallbackMessage(update) || handleSlashAssets(update);
    }

    private Boolean handleSlashAssets(Update update) {
        if (!Objects.equals(getMessageText(update), "/assets"))
            return false;

        sendMessageWithAssets(update, 0, false);
        return true;
    }

    private Boolean handleCallbackMessage(Update update) {
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(update);
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof ViewAssetsWithOffsetCallback cm))
            return false;

        sendMessageWithAssets(update, cm.getMonthOffset(), true);
        return true;
    }

    private void sendMessageWithAssets(Update update, int monthOffset, boolean edit) {
        var date = LocalDate.now();
        var user = getTelegramUser(update);
        var message = assetsResponseFactory.getTotalAssets(user, monthOffset);
        var keyboard = new InlineKeyboardMarkup().addRow(
                new ViewAssetsWithOffsetCallback(monthOffset - 1).asPrevMonthButton(date, monthOffset - 1),
                new ViewAssetsWithOffsetCallback(monthOffset + 1).asNextMonthButton(date, monthOffset + 1)
        );

        if (edit)
            editMessage(update, message, keyboard);
        else
            sendMessage(update, message, keyboard);
    }

}
