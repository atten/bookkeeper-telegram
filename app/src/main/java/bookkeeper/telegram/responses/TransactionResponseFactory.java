package bookkeeper.telegram.responses;

import bookkeeper.entities.AccountTransaction;
import bookkeeper.enums.Expenditure;
import bookkeeper.telegram.callbacks.ExpenditurePickCallback;
import bookkeeper.telegram.callbacks.TransactionApproveCallback;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

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

        var totalItemsVerbose = String.format(
            getTextPlural(transactions.size(), "Добавлена запись", "Добавлены %s записи", "Добавлено %s записей"),
            transactions.size()
        );

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

    public static InlineKeyboardMarkup getResponseKeyboard(List<AccountTransaction> transactions) {
        if (transactions.size() == 1) {
            var transaction = transactions.get(0);
            return getResponseKeyboard(transaction);
        }
        var kb = new InlineKeyboardMarkup();
        return kb.addRow(new InlineKeyboardButton("Уточнить категории"), new InlineKeyboardButton("Подтвердить все"));
    }

    public static InlineKeyboardMarkup getResponseKeyboard(AccountTransaction transaction) {
        var kb = new InlineKeyboardMarkup();
        var button1 = new ExpenditurePickCallback(transaction.getId()).asButton("Уточнить категорию");
        var button2 = new TransactionApproveCallback(transaction.getId()).asButton("Подтвердить");

        if (transaction.isApproved())
            kb.addRow(button1);
        else
            kb.addRow(button1, button2);

        return kb;
    }

    private static String getTextPlural(Integer count, String single, String few, String many) {
        if (count == 0)
            return many;
        if (count % 10 == 1)
            return single;
        if (count % 10 <= 4)
            return few;
        return many;
    }
}
