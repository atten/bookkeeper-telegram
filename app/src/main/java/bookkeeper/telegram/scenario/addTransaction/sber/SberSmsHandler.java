package bookkeeper.telegram.scenario.addTransaction.sber;

import bookkeeper.service.matcher.ExpenditureMatcherByMerchant;
import bookkeeper.service.repository.AccountRepository;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.telegram.scenario.addTransaction.AbstractTransactionRecordHandler;

import javax.inject.Inject;


/**
 * Scenario: user stores transactions.
 */
class SberSmsHandler extends AbstractTransactionRecordHandler {

    @Inject
    SberSmsHandler(AccountRepository accountRepository, AccountTransactionRepository transactionRepository, ExpenditureMatcherByMerchant expenditureMatcherByMerchant) {
        super(transactionRepository, new TransactionParserRegistryFactorySber(accountRepository, expenditureMatcherByMerchant).create());
    }
}