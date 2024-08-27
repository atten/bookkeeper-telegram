package bookkeeper.telegram.scenario.addTransaction.freehand;

import bookkeeper.dao.AccountRepository;
import bookkeeper.dao.AccountTransactionRepository;
import bookkeeper.service.matcher.ExpenditureMatcherByMerchant;
import bookkeeper.service.parser.SpendingParserRegistry;
import bookkeeper.service.registry.TransactionParserRegistry;
import bookkeeper.telegram.scenario.addTransaction.freehand.matcher.FreehandAccountMatcher;
import bookkeeper.telegram.scenario.addTransaction.freehand.matcher.FreehandAmountMatcher;
import bookkeeper.telegram.scenario.addTransaction.freehand.matcher.FreehandExpenditureMatcher;

import java.time.Instant;
import java.util.Optional;

class TransactionParserRegistryFactoryFreehand {
    private final AccountRepository accountRepository;
    private final AccountTransactionRepository transactionRepository;
    private final ExpenditureMatcherByMerchant expenditureMatcherByMerchant;

    TransactionParserRegistryFactoryFreehand(AccountRepository accountRepository, AccountTransactionRepository transactionRepository, ExpenditureMatcherByMerchant expenditureMatcherByMerchant) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.expenditureMatcherByMerchant = expenditureMatcherByMerchant;
    }

    TransactionParserRegistry create() {
        return new TransactionParserRegistry()
            .addAccountMatcher(new FreehandAccountMatcher(accountRepository, transactionRepository))
            .addAmountMatcher(new FreehandAmountMatcher())
            .addTimestampMatcher(spending -> Optional.of(Instant.now()))
            .setSpendingParserRegistry(SpendingParserRegistry.ofProvider("freehand"))
            .addExpenditureMatcher(expenditureMatcherByMerchant)
            .addExpenditureMatcher(new FreehandExpenditureMatcher());
    }
}
