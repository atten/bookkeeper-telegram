package bookkeeper.telegram.scenarios.store.tinkoff;

import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.repositories.AccountTransactionRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.services.matchers.ExpenditureMatcherByMerchant;
import bookkeeper.telegram.shared.AbstractTransactionRecordHandler;
import com.pengrad.telegrambot.TelegramBot;


/**
 * Scenario: user stores transactions.
 */
public class TinkoffSmsHandler extends AbstractTransactionRecordHandler {

    public TinkoffSmsHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountRepository accountRepository, AccountTransactionRepository transactionRepository, ExpenditureMatcherByMerchant expenditureMatcherByMerchant) {
        super(bot, telegramUserRepository, transactionRepository, new TransactionParserRegistryFactoryTinkoff(accountRepository, expenditureMatcherByMerchant).create());
    }
}