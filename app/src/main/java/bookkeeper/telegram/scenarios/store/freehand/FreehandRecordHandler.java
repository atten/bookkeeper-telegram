package bookkeeper.telegram.scenarios.store.freehand;

import bookkeeper.services.matchers.ExpenditureMatcherByMerchant;
import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.repositories.AccountTransactionRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractTransactionRecordHandler;
import com.pengrad.telegrambot.TelegramBot;


/**
 * Scenario: user stores transactions.
 */
public class FreehandRecordHandler extends AbstractTransactionRecordHandler {

    public FreehandRecordHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountRepository accountRepository, AccountTransactionRepository transactionRepository, ExpenditureMatcherByMerchant expenditureMatcherByMerchant) {
        super(bot, telegramUserRepository, transactionRepository, new TransactionParserRegistryFactoryFreehand(accountRepository, transactionRepository, expenditureMatcherByMerchant).create());
    }
}