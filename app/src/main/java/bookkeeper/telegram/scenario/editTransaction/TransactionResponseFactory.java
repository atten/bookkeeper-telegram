package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.dao.entity.AccountTransaction;
import bookkeeper.enums.Expenditure;
import bookkeeper.service.telegram.KeyboardUtils;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static bookkeeper.service.telegram.StringUtils.*;

public class TransactionResponseFactory {

    public static String getResponseMessage(List<AccountTransaction> transactions) {
        if (transactions.isEmpty()) {
            return "Не добавлено ни одной записи";
        }

        // build counter for each expenditure
        Map<Expenditure, AtomicLong> counterMap = new HashMap<>();
        transactions.forEach(transaction -> {
            counterMap.putIfAbsent(transaction.getExpenditure(), new AtomicLong(0));
            counterMap.get(transaction.getExpenditure()).incrementAndGet();
        });

        var totalItemsVerbose = pluralizeTemplate(transactions.size(), "Добавлена запись", "Добавлены %s записи", "Добавлено %s записей");
        var items = new StringJoiner("\n");

        if (counterMap.size() == 1) {
            var transaction = transactions.get(0);
            var item = "в *%s*".formatted(transaction.getExpenditure().getVerboseName());
            if (!isTransactionRecent(transaction)) {
                // add date (e.g. 10.01.23) if transaction was added earlier than 12h ago
                item += " " + getDateShort(transaction.date());
            }
            items.add(item);
        } else {
            counterMap
                .entrySet()
                .stream()
                .map(entry -> "• %s в *%s*".formatted(entry.getValue(), entry.getKey().getVerboseName()))
                .forEach(items::add);
        }

        var totalAmount = transactions.stream().map(AccountTransaction::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        var account = transactions.get(0).getAccount();
        var accountVerbose = "на счёт %s".formatted(account.getName());
        var totalAccountVerbose = getAmount(totalAmount, account);

        return totalItemsVerbose + " " + accountVerbose + ":\n" + items + "\n" + "на сумму " + totalAccountVerbose;
    }

    public static String getResponseMessage(AccountTransaction transaction) {
        return getResponseMessage(List.of(transaction));
    }

    public static String getResponseMessage(AccountTransaction transaction, Integer remainingCount) {
        String remaining;
        if (remainingCount == 0)
            remaining = "Это последняя запись.";
        else
            remaining = "Осталось: %s".formatted(remainingCount);

        var response = new StringJoiner("\n\n");
        response.add("`%s`".formatted(transaction.getRaw()));
        response.add(getResponseMessage(transaction));
        response.add(remaining);
        return response.toString();
    }

    public static InlineKeyboardMarkup getResponseKeyboard(List<AccountTransaction> transactions) {
        if (transactions.isEmpty()) {
            return new InlineKeyboardMarkup();
        } else if (transactions.size() == 1) {
            var transaction = transactions.get(0);
            return getResponseKeyboard(transaction);
        }
        var kb = new InlineKeyboardMarkup();
        var transactionIds = transactions.stream().map(AccountTransaction::getId).toList();
        var approvedCount = transactions.stream().map(AccountTransaction::isApproved).filter(aBoolean -> aBoolean).count();

        var button1 = new EditTransactionBulkCallback(transactionIds, transactionIds).asButton("Разобрать");
        var button2 = new ApproveTransactionBulkCallback(transactionIds).asButton("Подтвердить все");
        var button3 = new RemoveTransactionBulkCallback(transactionIds).asButton("Отменить все");

        if (approvedCount == transactionIds.size())
            return kb.addRow(button1);

        return kb.addRow(button1, button2, button3);
    }

    public static InlineKeyboardMarkup getResponseKeyboard(AccountTransaction transaction) {
        var transactionId = transaction.getId();
        var kb = new InlineKeyboardMarkup();

        if (transaction.isApproved()) {
            var unapproveButton = new UnapproveTransactionCallback(transactionId).asButton(ICON_UNAPPROVE + "Изменить");
            return kb.addRow(unapproveButton);
        }

        var selectExpenditureButton = new SelectExpenditureCallback(transactionId).asButton(ICON_EXPENDITURE + " Категория");
        var prevMonthButton = new ShiftTransactionMonthCallback(transactionId, -1).asPrevMonthButton(transaction.date(), "В %s");
        var nextMonthButton = new ShiftTransactionMonthCallback(transactionId, +1).asNextMonthButton(transaction.date(), "В %s");
        var accountButton = new SelectAccountCallback(transactionId).asButton(ICON_ACCOUNT + " Счёт");
        var removeButton = new RemoveTransactionCallback(transactionId).asButton(ICON_DELETE + " Отмена");
        var approveButton = new ApproveTransactionCallback(transactionId).asButton(ICON_APPROVE + " Подтвердить");

        var buttons = List.of(selectExpenditureButton, prevMonthButton, nextMonthButton, accountButton, removeButton, approveButton);
        return KeyboardUtils.createMarkupWithFixedColumns(buttons, 3);
    }

    public static InlineKeyboardMarkup getResponseKeyboard(AccountTransaction transaction, List<Long> allTransactionIds, List<Long> pendingTransactionIds) {
        var transactionId = transaction.getId();
        var buttons = new ArrayList<InlineKeyboardButton>();

        var selectExpenditureButton = new SelectExpenditureCallback(transactionId).setTransactionIds(allTransactionIds, pendingTransactionIds).asButton(ICON_EXPENDITURE + " Категория");
        var prevMonthButton = new ShiftTransactionMonthCallback(transactionId, -1).setTransactionIds(allTransactionIds, pendingTransactionIds).asPrevMonthButton(transaction.date(), "В %s");
        var nextMonthButton = new ShiftTransactionMonthCallback(transactionId, +1).setTransactionIds(allTransactionIds, pendingTransactionIds).asNextMonthButton(transaction.date(), "В %s");
        var accountButton = new SelectAccountCallback(transactionId).setTransactionIds(allTransactionIds, pendingTransactionIds).asButton(ICON_ACCOUNT + " Счёт");
        var overviewButton = new OverviewTransactionsCallback(transactionId).setTransactionIds(allTransactionIds, pendingTransactionIds).asButton("Готово");

        buttons.add(selectExpenditureButton);
        buttons.add(prevMonthButton);
        buttons.add(nextMonthButton);
        buttons.add(accountButton);

        var showApproveButton = !transaction.isApproved() || !pendingTransactionIds.isEmpty();
        if (showApproveButton) {
            var approveButtonText = transaction.isApproved() ? "Далее" : ICON_APPROVE + " Подтвердить";
            var approveButton = new ApproveTransactionCallback(transactionId).setTransactionIds(allTransactionIds, pendingTransactionIds).asButton(approveButtonText);
            buttons.add(approveButton);
        }

        buttons.add(overviewButton);
        return KeyboardUtils.createMarkupWithFixedColumns(buttons, 3);
    }

    private static boolean isTransactionRecent(AccountTransaction transaction) {
        return transaction.age().abs().toHours() < 12;
    }
}
