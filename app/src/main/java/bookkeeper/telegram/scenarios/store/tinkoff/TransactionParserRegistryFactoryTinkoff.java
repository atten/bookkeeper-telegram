package bookkeeper.telegram.scenarios.store.tinkoff;

import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.matchers.ExpenditureMatcherByMerchant;
import bookkeeper.telegram.scenarios.store.tinkoff.matchers.TinkoffAccountMatcher;
import bookkeeper.telegram.scenarios.store.tinkoff.matchers.TinkoffAmountMatcher;
import bookkeeper.telegram.scenarios.store.tinkoff.matchers.TinkoffTimestampMatcher;
import bookkeeper.telegram.scenarios.store.tinkoff.parsers.*;
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
            .addSpendingParser(new TinkoffPurchaseSmsParser())
            .addSpendingParser(new TinkoffPurchaseSmsWithDateParser())
            .addSpendingParser(new TinkoffFpsPurchaseSmsParser())
            .addSpendingParser(new TinkoffTransferSmsParser())
            .addSpendingParser(new TinkoffRecurringChargeSmsParser())
            .addExpenditureMatcher(expenditureMatcherByMerchant);
    }
}
