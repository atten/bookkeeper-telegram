package bookkeeper.telegram.scenario.addAccount;

import bookkeeper.service.repository.AccountRepository;
import bookkeeper.service.repository.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Scenario: User adds new account.
 */
class AddAccountHandler extends AbstractHandler {
    private final AccountRepository accountRepository;

    @Inject
    AddAccountHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountRepository accountRepository) {
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
            var lines = List.of(
                String.format("Синтаксис: `%s [account name] [currency]`", cmd),
                String.format("Пример: `%s копилка USD`", cmd)
            );

            sendMessage(update, String.join("\n", lines));
            return true;
        }

        if (arguments.size() < 2) {
            return false;
        }

        var accountName = String.join(" ", arguments.subList(0, arguments.size() - 1));
        Currency currency;
        try {
            currency = Currency.getInstance(arguments.get(arguments.size() - 1).toUpperCase());
        } catch (IllegalArgumentException e) {
            return false;
        }

        accountRepository.getOrCreate(accountName, currency, getTelegramUser(update));
        sendMessage(update, "Готово!");
        return true;
    }
}
