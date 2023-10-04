package bookkeeper.telegram.scenario.viewMonthlyExpenses;

import bookkeeper.entity.TelegramUser;
import bookkeeper.enums.Expenditure;
import bookkeeper.service.repository.AccountRepository;
import bookkeeper.service.repository.AccountTransactionRepository;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.math.BigDecimal;
import java.util.*;

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

            for (var expenditure : amountByExpenditure.keySet()) {
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

                lines.add(String.format(formatString, expenditure.getVerboseName(), roundedAmountString(amount)));
            }

            lines.add("```");
        }

        lines.add(String.format("*\uD83D\uDCDA Всего за %s*", getMonthYearShort(monthOffset)));
        lines.add("```");
        lines.add(String.format("%-7s %s", "Расходы", getRoundedAmountMulti(creditByCurrency)));
        lines.add(String.format("%-7s %s", "Доходы", getRoundedAmountMulti(debitByCurrency)));
        lines.add(String.format("%-7s %s", "Баланс", getRoundedAmountMulti(allByCurrency)));
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
        return String.format("% ,.0f", value.negate()).replace("-", "+");
    }
}
