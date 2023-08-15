package bookkeeper.services.registries.factories;

import bookkeeper.repositories.AccountRepository;
import bookkeeper.services.matchers.shared.ExpenditureMatcherByMerchant;
import bookkeeper.services.matchers.tinkoff.TinkoffAccountMatcher;
import bookkeeper.services.matchers.tinkoff.TinkoffAmountMatcher;
import bookkeeper.services.matchers.tinkoff.TinkoffTimestampMatcher;
import bookkeeper.services.parsers.tinkoff.TinkoffPurchaseSmsParser;
import bookkeeper.services.parsers.tinkoff.TinkoffPurchaseSmsWithDateParser;
import bookkeeper.services.parsers.tinkoff.TinkoffTransferSmsParser;
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
            .addSpendingParser(new TinkoffTransferSmsParser())
            .addExpenditureMatcher(expenditureMatcherByMerchant);
    }
}
