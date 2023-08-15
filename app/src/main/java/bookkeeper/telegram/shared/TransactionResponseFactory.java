package bookkeeper.telegram.shared;

import bookkeeper.entities.AccountTransaction;
import bookkeeper.enums.Expenditure;
import bookkeeper.telegram.scenarios.refine.SelectExpenditureCallback;
import bookkeeper.telegram.scenarios.refine.TransactionApproveBulkCallback;
import bookkeeper.telegram.scenarios.refine.TransactionApproveCallback;
import bookkeeper.telegram.scenarios.refine.TransactionEditBulkCallback;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class TransactionResponseFactory {

    public static String getResponseMessage(List<AccountTransaction> transactions) {
        if (transactions.size() == 0) {
            return "Не добавлено ни одной записи";
        }

        // build counter for each expenditure
        Map<Expenditure, AtomicLong> counterMap = new HashMap<>();
        transactions.forEach(transaction -> {
            counterMap.putIfAbsent(transaction.getExpenditure(), new AtomicLong(0));
            counterMap.get(transaction.getExpenditure()).incrementAndGet();
        });

        var totalItemsVerbose = pluralizeTemplate(transactions.size(), "Добавлена запись", "Добавлены %s записи", "Добавлено %s записей");

        String statsVerbose;

        if (counterMap.size() == 1) {
            var expenditure = transactions.get(0).getExpenditure();
            statsVerbose = String.format("в категории \"%s\"", expenditure.getName());
        } else {
            totalItemsVerbose = totalItemsVerbose + ": ";
            statsVerbose = counterMap
                    .entrySet()
                    .stream()
                    .map(entry -> String.format("%s в \"%s\"", entry.getValue(), entry.getKey().getName()))
                    .reduce((s, s2) -> s + ", " + s2).orElse("");
        }

        var totalAmount = transactions.stream().map(AccountTransaction::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        var account = transactions.get(0).getAccount();
        var accountVerbose = String.format("на счет %s", account.getName());
        var totalAccountVerbose = String.format("стоимостью %s %s", totalAmount, account.getCurrency().getSymbol());

        return totalItemsVerbose + " " + statsVerbose + " " + accountVerbose + " " + totalAccountVerbose + ".";
    }

    public static String getResponseMessage(AccountTransaction transaction) {
        return getResponseMessage(List.of(transaction));
    }

    public static String getResponseMessage(AccountTransaction transaction, Integer remainingCount) {
        String remaining;
        if (remainingCount == 0)
            remaining = "Это последняя запись.";
        else
            remaining = String.format("Осталось: %s", remainingCount);
        return String.format("`%s`\n%s\n%s", transaction.getRaw(), getResponseMessage(transaction), remaining);
    }

    public static String getResponseMessage(String merchant, Expenditure expenditure) {
        return String.format("Категория *%s* будет использоваться по умолчанию для последующих записей `%s`.", expenditure.getName(), merchant);
    }

    public static InlineKeyboardMarkup getResponseKeyboard(List<AccountTransaction> transactions) {
        if (transactions.size() == 0) {
            return new InlineKeyboardMarkup();
        } else if (transactions.size() == 1) {
            var transaction = transactions.get(0);
            return getResponseKeyboard(transaction);
        }
        var kb = new InlineKeyboardMarkup();
        var transactionIds = transactions.stream().map(AccountTransaction::getId).collect(Collectors.toList());
        var approvedCount = transactions.stream().map(AccountTransaction::isApproved).filter(aBoolean -> aBoolean).count();

        var button1 = new TransactionEditBulkCallback(transactionIds).asButton("Разобрать");
        var button2 = new TransactionApproveBulkCallback(transactionIds).asButton("Подтвердить все");

        if (approvedCount == transactionIds.size())
            return kb.addRow(button1);

        return kb.addRow(button1, button2);
    }

    public static InlineKeyboardMarkup getResponseKeyboard(AccountTransaction transaction) {
        var kb = new InlineKeyboardMarkup();
        var button1 = new SelectExpenditureCallback(transaction.getId()).asButton("Уточнить категорию");
        var button2 = new TransactionApproveCallback(transaction.getId()).asButton("Подтвердить");

        if (transaction.isApproved())
            return kb.addRow(button1);

        return kb.addRow(button1, button2);
    }

    public static InlineKeyboardMarkup getResponseKeyboard(AccountTransaction transaction, List<Long> pendingTransactionIds) {
        var kb = new InlineKeyboardMarkup();
        var button1 = new SelectExpenditureCallback(transaction.getId()).setPendingTransactionIds(pendingTransactionIds).asButton("Уточнить категорию");
        var callback2 = new TransactionApproveCallback(transaction.getId()).setPendingTransactionIds(pendingTransactionIds);

        if (transaction.isApproved()) {
            if (pendingTransactionIds.size() > 0)
                return kb.addRow(button1, callback2.asButton("Далее"));
            else
                return kb.addRow(button1, callback2.asButton("Готово"));
        }

        return kb.addRow(button1, callback2.asButton("Подтвердить"));
    }

    public static String pluralize(Integer count, String single, String few, String many) {
        var n = count % 100;
        if (count == 0)
            return many;
        if (n >= 5 && n <= 20)
            return many;
        if (count % 10 == 1)
            return single;
        if (count % 10 <= 4)
            return few;
        return many;
    }

    /**
     * pluralize for %s-strings
     */
    public static String pluralizeTemplate(Integer count, String single, String few, String many) {
        return String.format(pluralize(count, single, few, many), count);
    }
}
