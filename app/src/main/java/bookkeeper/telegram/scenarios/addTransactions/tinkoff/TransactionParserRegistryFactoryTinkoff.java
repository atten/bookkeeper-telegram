package bookkeeper.telegram.scenarios.addTransactions.tinkoff;

import bookkeeper.services.parsers.SpendingParserRegistry;
import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.matchers.ExpenditureMatcherByMerchant;
import bookkeeper.telegram.scenarios.addTransactions.tinkoff.matchers.TinkoffAccountMatcher;
import bookkeeper.telegram.scenarios.addTransactions.tinkoff.matchers.TinkoffAmountMatcher;
import bookkeeper.telegram.scenarios.addTransactions.tinkoff.matchers.TinkoffTimestampMatcher;
import bookkeeper.services.registries.TransactionParserRegistry;

public class TransactionParserRegistryFactoryTinkoff {
    private final AccountRepository accountRepository;
    private final ExpenditureMatcherByMerchant expenditureMatcherByMerchant;

    public TransactionParserRegistryFactoryTinkoff(AccountRepository accountRepository, ExpenditureMatcherByMerchant expenditureMatcherByMerchant) {
        this.accountRepository = accountRepository;
        this.expenditureMatcherByMerchant = expenditureMatcherByMerchant;
    }

    public TransactionParserRegistry create() {
        return new TransactionParserRegistry()
            .addAccountMatcher(new TinkoffAccountMatcher(accountRepository))
            .addAmountMatcher(new TinkoffAmountMatcher())
            .addTimestampMatcher(new TinkoffTimestampMatcher())
            .setSpendingParserRegistry(SpendingParserRegistry.ofProvider("tinkoff"))
            .addExpenditureMatcher(expenditureMatcherByMerchant);
    }
}
