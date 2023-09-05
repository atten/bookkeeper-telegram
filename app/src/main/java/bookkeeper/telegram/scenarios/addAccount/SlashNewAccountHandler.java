package bookkeeper.telegram.scenarios.addAccount;

import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.util.Arrays;
import java.util.Currency;
import java.util.stream.Collectors;

/**
 * Scenario: User adds new account.
 */
public class SlashNewAccountHandler extends AbstractHandler {
    private final AccountRepository accountRepository;

    public SlashNewAccountHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountRepository accountRepository) {
        super(bot, telegramUserRepository);
        this.accountRepository = accountRepository;
    }

    /**
     * Display help text (if sent without params), or create account with given params.
     */
    @Override
    public Boolean handle(Update update) {
        var cmd = "/new_account";
        var msg = getMessageText(update);
        if (!msg.startsWith(cmd))
            return false;

        var arguments = Arrays.stream(msg.split(" ")).skip(1).collect(Collectors.toList());

        if (arguments.isEmpty()) {
            sendMessage(update, String.format("Синтаксис: %s [account_name] [currency]\nПример: %s копилка USD", cmd, cmd));
            return true;
        }

        if (arguments.size() != 2) {
            return false;
        }

        var accountName = arguments.get(0);
        Currency currency;
        try {
            currency = Currency.getInstance(arguments.get(1).toUpperCase());
        } catch (IllegalArgumentException e) {
            return false;
        }

        accountRepository.getOrCreate(accountName, currency, getTelegramUser(update));
        sendMessage(update, "Готово!");
        return true;
    }
}
