package bookkeeper.telegram.scenarios.addTransactions.freehand;

import bookkeeper.services.matchers.ExpenditureMatcherByMerchant;
import bookkeeper.services.parsers.SpendingParserRegistry;
import bookkeeper.services.registries.TransactionParserRegistry;
import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.repositories.AccountTransactionRepository;
import bookkeeper.telegram.scenarios.addTransactions.freehand.matchers.ExpenditureMatcherByDescription;
import bookkeeper.telegram.scenarios.addTransactions.freehand.matchers.FreehandAmountMatcher;
import bookkeeper.telegram.scenarios.addTransactions.freehand.matchers.LastUsedAccountMatcher;

import java.time.Instant;

public class TransactionParserRegistryFactoryFreehand {
    private final AccountRepository accountRepository;
    private final AccountTransactionRepository transactionRepository;
    private final ExpenditureMatcherByMerchant expenditureMatcherByMerchant;

    public TransactionParserRegistryFactoryFreehand(AccountRepository accountRepository, AccountTransactionRepository transactionRepository, ExpenditureMatcherByMerchant expenditureMatcherByMerchant) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.expenditureMatcherByMerchant = expenditureMatcherByMerchant;
    }

    public TransactionParserRegistry create() {
        return new TransactionParserRegistry()
            .addAccountMatcher(new LastUsedAccountMatcher(accountRepository, transactionRepository))
            .addAmountMatcher(new FreehandAmountMatcher())
            .addTimestampMatcher(spending -> Instant.now())
            .setSpendingParserRegistry(SpendingParserRegistry.ofProvider("freehand"))
            .addExpenditureMatcher(expenditureMatcherByMerchant)
            .addExpenditureMatcher(new ExpenditureMatcherByDescription());
    }
}
