package bookkeeper.telegram.scenario.addTransaction.freehand;

import bookkeeper.enums.HandlerPriority;
import bookkeeper.service.matcher.ExpenditureMatcherByMerchant;
import bookkeeper.service.repository.AccountRepository;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.telegram.scenario.addTransaction.AbstractTransactionRecordHandler;

import javax.inject.Inject;


/**
 * Scenario: user stores transactions.
 */
class FreehandRecordHandler extends AbstractTransactionRecordHandler {

    @Override
    public HandlerPriority getPriority() {
        return HandlerPriority.LOW_MESSAGE;
    }

    @Inject
    FreehandRecordHandler(AccountRepository accountRepository, AccountTransactionRepository transactionRepository, ExpenditureMatcherByMerchant expenditureMatcherByMerchant) {
        super(transactionRepository, new TransactionParserRegistryFactoryFreehand(accountRepository, transactionRepository, expenditureMatcherByMerchant).create());
    }
}