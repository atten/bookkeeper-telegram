package bookkeeper.telegram.scenario.addTransaction.tinkoff;

import bookkeeper.service.repository.AccountRepository;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.service.repository.TelegramUserRepository;
import bookkeeper.service.matcher.ExpenditureMatcherByMerchant;
import bookkeeper.telegram.shared.AbstractTransactionRecordHandler;
import com.pengrad.telegrambot.TelegramBot;

import javax.inject.Inject;


/**
 * Scenario: user stores transactions.
 */
class TinkoffSmsHandler extends AbstractTransactionRecordHandler {

    @Inject
    TinkoffSmsHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountRepository accountRepository, AccountTransactionRepository transactionRepository, ExpenditureMatcherByMerchant expenditureMatcherByMerchant) {
        super(bot, telegramUserRepository, transactionRepository, new TransactionParserRegistryFactoryTinkoff(accountRepository, expenditureMatcherByMerchant).create());
    }
}