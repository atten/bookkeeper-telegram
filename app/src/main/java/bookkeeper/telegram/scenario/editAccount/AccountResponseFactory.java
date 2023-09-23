package bookkeeper.telegram.scenario.editAccount;

import bookkeeper.entity.Account;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.StringJoiner;

import static bookkeeper.telegram.shared.StringUtils.ICON_ACCOUNT;

class AccountResponseFactory {
    static String getMessageText(Account account) {
        var lines = new StringJoiner("\n");
        lines
            .add(ICON_ACCOUNT + " Редактирование счёта\n")
            .add("Имя: " + account.getName())
            .add("Валюта: " + account.getCurrency().getCurrencyCode())
            .add(String.format("Заметки: %s", account.getNotes() != null ? "```\n" + account.getNotes() + "```" : "нет"));

        return lines.toString();
    }

    static InlineKeyboardMarkup getMessageKeyboard(Account account) {
        return new InlineKeyboardMarkup()
            .addRow(
                new RenameAccountCallback(account.getId()).asButton("Переименовать"),
                new SetAccountNotesCallback(account.getId()).asButton("Заметки")
            ).addRow(
                new ListAccountsCallback().asButton("Назад")
            );
    }
}
