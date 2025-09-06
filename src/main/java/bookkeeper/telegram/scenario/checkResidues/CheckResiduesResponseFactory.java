package bookkeeper.telegram.scenario.checkResidues;

import bookkeeper.dao.entity.AccountTransaction;
import bookkeeper.dao.entity.TelegramUser;
import bookkeeper.dao.repository.AccountTransactionRepository;
import bookkeeper.service.parser.Spending;
import bookkeeper.service.parser.SpendingParserRegistry;
import bookkeeper.service.query.AssetQuery;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.StringJoiner;

import static bookkeeper.service.telegram.StringUtils.*;

public class CheckResiduesResponseFactory {
    private final AssetQuery assetQuery;
    private final AccountTransactionRepository transactionRepository;
    private final SpendingParserRegistry spendingParserRegistry = SpendingParserRegistry.ofAllParsers();

    @Inject
    CheckResiduesResponseFactory(AssetQuery assetQuery, AccountTransactionRepository transactionRepository) {
        this.assetQuery = assetQuery;
        this.transactionRepository = transactionRepository;
    }

    String getResiduesCheckResult(TelegramUser user) {
        var monthOffset = 12; // include transactions recorded in future
        var assets = assetQuery.getMonthlyAssets(user, monthOffset);

        var content = new StringJoiner("\n\n");
        for (var asset : assets) {
            var latestTransactions = transactionRepository.findRecentAdded(asset.account(), 1);
            if (latestTransactions.isEmpty())
                continue;

            var transaction = latestTransactions.getFirst();
            var spending = getSpending(transaction);
            if (spending.getBalance().isEmpty())
                continue;

            var spendingBalance = spending.getBalance().get();
            var currency = asset.account().getCurrency();
            var message = "%s\nРазница: %s\nПосчитано: %s\nВведено: %s (`%s` от %s)".formatted(
                asset.account().getName(),
                getRoundedAmountSigned(spendingBalance.subtract(asset.balance()), currency),
                getRoundedAmount(asset.balance(), currency),
                getRoundedAmount(spendingBalance, currency),
                transaction.getRaw(),
                getDateShort(transaction.date())
            );
            content.add(message);
        }

        if (content.length() == 0) {
            content.add("Нет данных по остаткам счетов.");
        }

        return content.toString();
    }

    /**
     * restore spending from transaction raw message
     */
    private Spending getSpending(AccountTransaction transaction) {
        try {
            return spendingParserRegistry.parse(transaction.getRaw());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
