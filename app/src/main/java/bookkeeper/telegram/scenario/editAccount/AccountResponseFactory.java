package bookkeeper.telegram.scenario.editAccount;

import bookkeeper.entity.Account;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.StringJoiner;

import static bookkeeper.service.telegram.StringUtils.*;

class AccountResponseFactory {
    static String getMessageText(Account account) {
        var lines = new StringJoiner("\n");
        lines
            .add(ICON_ACCOUNT + " Редактирование счёта\n")
            .add("Имя: " + account.getName())
            .add("Валюта: " + account.getCurrency().getCurrencyCode())
            .add(getVisibilityDetails(account))
            .add(String.format("Заметки: %s", account.getNotes() != null ? "```\n" + account.getNotes() + "```" : "нет"));

        return lines.toString();
    }
    static InlineKeyboardMarkup getMessageKeyboard(Account account) {
        return new InlineKeyboardMarkup()
            .addRow(
                new RenameAccountCallback(account.getId()).asButton("Переименовать"),
                getSwitchAccountVisibilityButton(account),
                new SetAccountNotesCallback(account.getId()).asButton("Заметки")
            ).addRow(
                new ListAccountsCallback(account.isHidden()).asButton("Назад")
            );
    }

    private static String getVisibilityDetails(Account account) {
        if (account.isHidden()) {
            return ICON_HIDDEN + " Скрыт из списка счетов";
        } else {
            return ICON_VISIBLE + " Виден в списке счетов";
        }
    }

    private static InlineKeyboardButton getSwitchAccountVisibilityButton(Account account) {
        if (account.isHidden()) {
            return new SwitchAccountVisibilityCallback(account.getId(), false).asButton(ICON_VISIBLE + " Показать");
        } else {
            return new SwitchAccountVisibilityCallback(account.getId(), true).asButton(ICON_HIDDEN + " Скрыть");
        }
    }
}
