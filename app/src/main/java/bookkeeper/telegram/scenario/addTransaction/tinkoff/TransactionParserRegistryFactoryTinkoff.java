package bookkeeper.telegram.scenario.addTransaction.tinkoff;

import bookkeeper.service.parser.SpendingParserRegistry;
import bookkeeper.service.repository.AccountRepository;
import bookkeeper.service.matcher.ExpenditureMatcherByMerchant;
import bookkeeper.telegram.scenario.addTransaction.tinkoff.matcher.TinkoffAccountMatcher;
import bookkeeper.telegram.scenario.addTransaction.tinkoff.matcher.TinkoffAmountMatcher;
import bookkeeper.telegram.scenario.addTransaction.tinkoff.matcher.TinkoffTimestampMatcher;
import bookkeeper.service.registry.TransactionParserRegistry;

class TransactionParserRegistryFactoryTinkoff {
    private final AccountRepository accountRepository;
    private final ExpenditureMatcherByMerchant expenditureMatcherByMerchant;

    TransactionParserRegistryFactoryTinkoff(AccountRepository accountRepository, ExpenditureMatcherByMerchant expenditureMatcherByMerchant) {
        this.accountRepository = accountRepository;
        this.expenditureMatcherByMerchant = expenditureMatcherByMerchant;
    }

    TransactionParserRegistry create() {
        return new TransactionParserRegistry()
            .addAccountMatcher(new TinkoffAccountMatcher(accountRepository))
            .addAmountMatcher(new TinkoffAmountMatcher())
            .addTimestampMatcher(new TinkoffTimestampMatcher())
            .setSpendingParserRegistry(SpendingParserRegistry.ofProvider("tinkoff"))
            .addExpenditureMatcher(expenditureMatcherByMerchant);
    }
}
