package bookkeeper.telegram.scenario.addTransaction.freehand;

import bookkeeper.service.matcher.ExpenditureMatcherByMerchant;
import bookkeeper.service.repository.AccountRepository;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.service.repository.TelegramUserRepository;
import bookkeeper.telegram.shared.AbstractTransactionRecordHandler;
import com.pengrad.telegrambot.TelegramBot;

import javax.inject.Inject;


/**
 * Scenario: user stores transactions.
 */
class FreehandRecordHandler extends AbstractTransactionRecordHandler {

    @Inject
    FreehandRecordHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountRepository accountRepository, AccountTransactionRepository transactionRepository, ExpenditureMatcherByMerchant expenditureMatcherByMerchant) {
        super(bot, telegramUserRepository, transactionRepository, new TransactionParserRegistryFactoryFreehand(accountRepository, transactionRepository, expenditureMatcherByMerchant).create());
    }
}