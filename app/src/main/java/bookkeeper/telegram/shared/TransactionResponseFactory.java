package bookkeeper.telegram.shared;

import bookkeeper.entities.AccountTransaction;
import bookkeeper.enums.Expenditure;
import bookkeeper.telegram.scenarios.editTransactions.*;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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

        var totalItemsVerbose = pluralizeTemplate(transactions.size(), "Добавлена", "Добавлены %s записи", "Добавлено %s записей");

        String statsVerbose;

        if (counterMap.size() == 1) {
            var transaction = transactions.get(0);
            statsVerbose = String.format("в *%s*", transaction.getExpenditure().getVerboseName());
            if (!isTransactionRecent(transaction)) {
                // add date (e.g. 10.01.23) if transaction was added earlier than 12h ago
                statsVerbose += " " + transaction.date().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
            }
        } else {
            totalItemsVerbose = totalItemsVerbose + ": ";
            statsVerbose = counterMap
                    .entrySet()
                    .stream()
                    .map(entry -> String.format("%s в *%s*", entry.getValue(), entry.getKey().getVerboseName()))
                    .reduce((s, s2) -> s + ", " + s2).orElse("");
        }

        var totalAmount = transactions.stream().map(AccountTransaction::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        var account = transactions.get(0).getAccount();
        var accountVerbose = String.format("на счет %s", account.getName());
        var totalAccountVerbose = String.format(
            "стоимостью %s %s",
            totalAmount.negate().toString().replace("-", "+"),
            account.getCurrency().getSymbol()
        );

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
        return String.format("`%s`\n\n%s\n\n%s", transaction.getRaw(), getResponseMessage(transaction), remaining);
    }

    public static String getResponseMessage(String merchant, Expenditure expenditure) {
        return String.format("Категория *%s* будет использоваться по умолчанию для последующих записей `%s`.", expenditure.getVerboseName(), merchant);
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

        var button1 = new EditTransactionBulkCallback(transactionIds).asButton("Разобрать");
        var button2 = new ApproveTransactionBulkCallback(transactionIds).asButton("Подтвердить все");

        if (approvedCount == transactionIds.size())
            return kb.addRow(button1);

        return kb.addRow(button1, button2);
    }

    public static InlineKeyboardMarkup getResponseKeyboard(AccountTransaction transaction) {
        var transactionId = transaction.getId();

        var kb = new InlineKeyboardMarkup();
        var selectExpenditureButton = new SelectExpenditureCallback(transactionId).asButton("Уточнить категорию");
        var prevMonthButton = new ShiftTransactionMonthCallback(transactionId, -1).asButton(transaction.date());
        var nextMonthButton = new ShiftTransactionMonthCallback(transactionId, +1).asButton(transaction.date());
        var approveButton = new ApproveTransactionCallback(transactionId).asButton("Подтвердить");

        if (transaction.isApproved())
            return kb.addRow(selectExpenditureButton);

        return kb.addRow(selectExpenditureButton, prevMonthButton, nextMonthButton, approveButton);
    }

    public static InlineKeyboardMarkup getResponseKeyboard(AccountTransaction transaction, List<Long> pendingTransactionIds) {
        var transactionId = transaction.getId();
        var approveButtonText = "Подтвердить";

        if (transaction.isApproved()) {
            approveButtonText = "Далее";
            if (pendingTransactionIds.isEmpty())
                approveButtonText = "Готово";
        }

        var selectExpenditureButton = new SelectExpenditureCallback(transactionId).setPendingTransactionIds(pendingTransactionIds).asButton("Уточнить категорию");
        var prevMonthButton = new ShiftTransactionMonthCallback(transactionId, -1).setPendingTransactionIds(pendingTransactionIds).asButton(transaction.date());
        var nextMonthButton = new ShiftTransactionMonthCallback(transactionId, +1).setPendingTransactionIds(pendingTransactionIds).asButton(transaction.date());
        var approveButton = new ApproveTransactionCallback(transactionId).setPendingTransactionIds(pendingTransactionIds).asButton(approveButtonText);

        return new InlineKeyboardMarkup()
            .addRow(selectExpenditureButton, prevMonthButton, nextMonthButton)
            .addRow(approveButton);
    }

    private static String pluralize(Integer count, String single, String few, String many) {
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

    private static boolean isTransactionRecent(AccountTransaction transaction) {
        return transaction.age().abs().toHours() < 12;
    }
}
