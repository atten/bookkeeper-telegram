package bookkeeper.telegram.scenarios.review;

import bookkeeper.entities.TelegramUser;
import bookkeeper.enums.Expenditure;
import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.repositories.AccountTransactionRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ReviewResponseFactory {
    private final AccountRepository accountRepository;
    private final AccountTransactionRepository transactionRepository;

    public ReviewResponseFactory(AccountRepository accountRepository, AccountTransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public String getMonthlyExpenses(TelegramUser user, int monthDelta) {
        var lines = new ArrayList<String>();
        var creditByCurrency = new HashMap<Currency, BigDecimal>();
        var debitByCurrency = new HashMap<Currency, BigDecimal>();
        var allByCurrency = new HashMap<Currency, BigDecimal>();

        var accounts = accountRepository.findForUser(user);

        for (var account : accounts) {
            var currency = account.getCurrency();

            creditByCurrency.putIfAbsent(currency, BigDecimal.ZERO);
            debitByCurrency.putIfAbsent(currency, BigDecimal.ZERO);
            allByCurrency.putIfAbsent(currency, BigDecimal.ZERO);

            lines.add(String.format("*%s*:", account.getName()));

            for (var expenditure : Expenditure.enabledValues()) {
                var amount = transactionRepository.getAggregatedAmount(account, expenditure, monthDelta);
                var strAmount = amount.toString().replace("-", "+");
                var sign = amount.compareTo(BigDecimal.ZERO);

                allByCurrency.merge(currency, amount, BigDecimal::add);

                if (sign > 0) {
                    creditByCurrency.merge(currency, amount, BigDecimal::add);
                } else if (sign < 0) {
                    debitByCurrency.merge(currency, amount, BigDecimal::add);
                } else {
                    continue;
                }

                lines.add(String.format("%s: %s %s", expenditure.getVerboseName(), strAmount, account.getCurrency().getSymbol()));
            }
        }

        var totalCreditByCurrency = creditByCurrency
                .entrySet().stream()
                .map(i -> i.getValue().toString() + " " + i.getKey().getSymbol())
                .collect(Collectors.joining(", "));

        var totalDebitByCurrency = debitByCurrency
                .entrySet().stream()
                .map(i -> i.getValue().toString().replace("-", "") + " " + i.getKey().getSymbol())
                .collect(Collectors.joining(", "));

        var totalByCurrency = allByCurrency
                .entrySet().stream()
                .map(i -> i.getValue().toString().replace("-", "+") + " " + i.getKey().getSymbol())
                .collect(Collectors.joining(", "));

        lines.add("");
        lines.add("Всего расходов: " + totalCreditByCurrency);
        lines.add("Всего доходов: " + totalDebitByCurrency);
        lines.add("Баланс: " + totalByCurrency);

        return String.join("\n", lines);
    }
}
