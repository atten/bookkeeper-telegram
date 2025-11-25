package bookkeeper.telegram.scenario.editAccount;

import bookkeeper.dao.entity.Account;
import bookkeeper.service.query.AssetQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.StringJoiner;

import static bookkeeper.service.telegram.StringUtils.*;

class AccountResponseFactory {
    private final AssetQuery assetQuery;

    @Inject
    AccountResponseFactory(AssetQuery assetQuery) {
        this.assetQuery = assetQuery;
    }

    public String getMessageText(Account account) {
        var monthOffset = 12; // include transactions recorded in future
        var accountBalance = assetQuery
            .getMonthlyAssets(account.getTelegramUser(), monthOffset)
            .stream()
            .filter(asset -> asset.account().equals(account))
            .map(AssetQuery.Asset::balance)
            .findFirst()
            .orElse(BigDecimal.ZERO);

        var lines = new StringJoiner("\n");
        lines
            .add(ICON_ACCOUNT + " Редактирование счёта\n")
            .add("Имя: " + account.getName())
            .add("Баланс: %.2f %s".formatted(accountBalance, account.getCurrency().getCurrencyCode()))
            .add(getVisibilityDetails(account))
            .add("Заметки: %s".formatted(account.getNotes() != null ? "```\n" + account.getNotes() + "```" : "нет"));

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
