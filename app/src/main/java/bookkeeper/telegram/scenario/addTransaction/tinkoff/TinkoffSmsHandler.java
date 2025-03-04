package bookkeeper.telegram.scenario.addTransaction.tinkoff;

import bookkeeper.dao.AccountRepository;
import bookkeeper.dao.AccountTransactionRepository;
import bookkeeper.service.matcher.ExpenditureMatcherByMerchant;
import bookkeeper.telegram.scenario.addTransaction.AbstractTransactionRecordHandler;

import javax.inject.Inject;


/**
 * Scenario: user stores transactions.
 */
class TinkoffSmsHandler extends AbstractTransactionRecordHandler {

    @Inject
    TinkoffSmsHandler(AccountRepository accountRepository, AccountTransactionRepository transactionRepository, ExpenditureMatcherByMerchant expenditureMatcherByMerchant) {
        super(transactionRepository, new TransactionParserRegistryFactoryTinkoff(accountRepository, expenditureMatcherByMerchant).create());
    }
}