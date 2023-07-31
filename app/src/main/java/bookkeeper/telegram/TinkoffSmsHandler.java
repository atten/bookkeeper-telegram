package bookkeeper.telegram;

import bookkeeper.entities.AccountTransaction;
import bookkeeper.repositories.AccountRepository;
import bookkeeper.repositories.AccountTransactionRepository;
import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.services.matchers.shared.ExpenditureMatcherByMerchant;
import bookkeeper.services.registries.TransactionParserRegistry;
import bookkeeper.services.registries.factories.TransactionParserRegistryFactoryTinkoff;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.text.ParseException;


/**
 * Parse SMS text from Tinkoff and categorize related expenses.
 */
public class TinkoffSmsHandler extends AbstractHandler {
    private final TransactionParserRegistry transactionParserRegistry;
    private final AccountTransactionRepository transactionRepository;

    TinkoffSmsHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountRepository accountRepository, AccountTransactionRepository transactionRepository, ExpenditureMatcherByMerchant expenditureMatcherByMerchant) {
        super(bot, telegramUserRepository);
        this.transactionRepository = transactionRepository;
        this.transactionParserRegistry = new TransactionParserRegistryFactoryTinkoff(accountRepository, expenditureMatcherByMerchant).create();
    }

    /**
     * Take Raw SMS, Transform to AccountTransaction and put to AccountTransactionRepository.
     */
    @Override
    Boolean handle(Update update) {
        if (update.message() == null)
            return false;

        var smsList = update.message().text().split("\n");
        for (var message : smsList ) {
            AccountTransaction transaction;
            try {
                transaction = transactionParserRegistry.parse(message, getTelegramUser(update));
            } catch (ParseException e) {
                // provided sms was not parsed
                return false;
            }

            transactionRepository.save(transaction);
        }
        return true;

    }
}
