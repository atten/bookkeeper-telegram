package bookkeeper.telegram.scenario.addTransaction.sber;

import bookkeeper.dao.repository.AccountRepository;
import bookkeeper.service.matcher.ExpenditureMatcherByMerchant;
import bookkeeper.service.parser.SpendingParserRegistry;
import bookkeeper.service.registry.TransactionParserRegistry;
import bookkeeper.telegram.scenario.addTransaction.sber.matcher.SberAccountMatcher;
import bookkeeper.telegram.scenario.addTransaction.sber.matcher.SberAmountMatcher;

class TransactionParserRegistryFactorySber {
    private final AccountRepository accountRepository;
    private final ExpenditureMatcherByMerchant expenditureMatcherByMerchant;

    TransactionParserRegistryFactorySber(AccountRepository accountRepository, ExpenditureMatcherByMerchant expenditureMatcherByMerchant) {
        this.accountRepository = accountRepository;
        this.expenditureMatcherByMerchant = expenditureMatcherByMerchant;
    }

    TransactionParserRegistry create() {
        return new TransactionParserRegistry()
            .addAccountMatcher(new SberAccountMatcher(accountRepository))
            .addAmountMatcher(new SberAmountMatcher())
            .setSpendingParserRegistry(SpendingParserRegistry.ofProvider("sber"))
            .addExpenditureMatcher(expenditureMatcherByMerchant);
    }
}
