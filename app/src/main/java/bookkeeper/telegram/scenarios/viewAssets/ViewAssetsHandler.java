package bookkeeper.telegram.scenarios.viewAssets;

import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.repositories.AccountTransactionRepository;
import bookkeeper.services.repositories.AccountTransferRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.util.Objects;


/**
 * Scenario: user requests total assets.
 */
public class ViewAssetsHandler extends AbstractHandler {
    private final AssetsResponseFactory assetsResponseFactory;

    public ViewAssetsHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountRepository accountRepository, AccountTransactionRepository transactionRepository, AccountTransferRepository transferRepository) {
        super(bot, telegramUserRepository);
        this.assetsResponseFactory = new AssetsResponseFactory(accountRepository, transactionRepository, transferRepository);
    }

    /**
     * Display total assets overview
     */
    @Override
    public Boolean handle(Update update) {
        if (!Objects.equals(getMessageText(update), "/assets"))
            return false;

        var user = getTelegramUser(update);
        var message = assetsResponseFactory.getTotalAssets(user);
        sendMessage(update, message);

        return true;
    }

}