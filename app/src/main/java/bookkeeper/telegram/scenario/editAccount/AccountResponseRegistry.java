package bookkeeper.telegram.scenario.editAccount;

import bookkeeper.entity.Account;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.StringJoiner;

import static bookkeeper.telegram.shared.StringUtil.ICON_ACCOUNT;

class AccountResponseRegistry {
    static String getMessageText(Account account) {
        var lines = new StringJoiner("\n");
        lines
            .add(ICON_ACCOUNT + " Редактирование счёта\n")
            .add("Имя: " + account.getName())
            .add("Валюта: " + account.getCurrency().getCurrencyCode());

        return lines.toString();
    }

    static InlineKeyboardMarkup getMessageKeyboard(Account account) {
        return new InlineKeyboardMarkup(
            new ListAccountsCallback().asButton("Назад"),
            new RenameAccountCallback(account.getId()).asButton("Переименовать")
        );
    }
}
