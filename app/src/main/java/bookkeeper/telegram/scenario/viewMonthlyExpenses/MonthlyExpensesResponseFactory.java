package bookkeeper.telegram.scenario.viewMonthlyExpenses;

import bookkeeper.dao.AccountRepository;
import bookkeeper.dao.AccountTransactionRepository;
import bookkeeper.dao.entity.TelegramUser;
import bookkeeper.enums.Expenditure;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.StringJoiner;

import static bookkeeper.service.telegram.StringUtils.*;

class MonthlyExpensesResponseFactory {
    private final AccountRepository accountRepository;
    private final AccountTransactionRepository transactionRepository;

    MonthlyExpensesResponseFactory(AccountRepository accountRepository, AccountTransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    String getMonthlyExpenses(TelegramUser user, int monthOffset) {
        var lines = new StringJoiner("\n");
        var creditByCurrency = new HashMap<Currency, BigDecimal>();
        var debitByCurrency = new HashMap<Currency, BigDecimal>();
        var allByCurrency = new HashMap<Currency, BigDecimal>();
        var maxExpenditureLength = Expenditure.enabledValues().stream().map(expenditure -> expenditure.getVerboseName().length()).max(Comparator.naturalOrder()).orElse(0);
        var formatString = "%-" + maxExpenditureLength + "s %s";  // example: "%-15s %s"
        var accounts = accountRepository.filter(user);

        for (var account : accounts) {
            var currency = account.getCurrency();
            var amountByExpenditure = transactionRepository.getMonthlyAmount(account, monthOffset);

            if (amountByExpenditure.isEmpty())
                continue;

            creditByCurrency.putIfAbsent(currency, BigDecimal.ZERO);
            debitByCurrency.putIfAbsent(currency, BigDecimal.ZERO);
            allByCurrency.putIfAbsent(currency, BigDecimal.ZERO);

            lines.add("*" + getAccountDisplayName(account) + "*");
            lines.add("```");

            for (var expenditure : amountByExpenditure.keySet().stream().sorted().toList()) {
                var amount = amountByExpenditure.get(expenditure);
                var sign = amount.compareTo(BigDecimal.ZERO);

                allByCurrency.merge(currency, amount, BigDecimal::add);

                if (sign > 0) {
                    debitByCurrency.merge(currency, amount, BigDecimal::add);
                } else if (sign < 0) {
                    creditByCurrency.merge(currency, amount, BigDecimal::add);
                } else {
                    continue;
                }

                lines.add(formatString.formatted(expenditure.getVerboseName(), roundedAmountString(amount)));
            }

            lines.add("```");
        }

        lines.add("*\uD83D\uDCDA Всего за %s*".formatted(getMonthYearShort(monthOffset)));
        lines.add("```");
        lines.add("%-7s %s".formatted("Расходы", getRoundedAmountMulti(creditByCurrency)));
        lines.add("%-7s %s".formatted("Доходы", getRoundedAmountMulti(debitByCurrency)));
        lines.add("%-7s %s".formatted("Баланс", getRoundedAmountMulti(allByCurrency)));
        lines.add("```");

        return lines.toString();
    }

    static InlineKeyboardMarkup getMonthlyExpensesKeyboard(int monthOffset) {
        var keyboard = new InlineKeyboardMarkup()
                .addRow(new SelectMonthlyExpendituresCallback(monthOffset).asButton("Разобрать"));

        var prevMonthButton = new ViewMonthlyExpensesWithOffsetCallback(monthOffset - 1).asPrevMonthButton(monthOffset - 1);
        var nextMonthButton = new ViewMonthlyExpensesWithOffsetCallback(monthOffset + 1).asNextMonthButton(monthOffset + 1);
        return keyboard.addRow(prevMonthButton, nextMonthButton);
    }

    private String roundedAmountString(BigDecimal value) {
        return "% ,.0f".formatted(value.negate()).replace("-", "+");
    }
}
