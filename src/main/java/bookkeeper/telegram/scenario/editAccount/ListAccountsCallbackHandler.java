package bookkeeper.telegram.scenario.editAccount;

import bookkeeper.dao.repository.AccountRepository;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.KeyboardUtils;
import bookkeeper.service.telegram.Request;
import bookkeeper.telegram.scenario.viewAssets.ViewAssetsCallback;

import javax.inject.Inject;
import java.util.Objects;

import static bookkeeper.service.telegram.StringUtils.ICON_ACCOUNT;

/**
 * Scenario: User requests accounts list to pick one and change attributes.
 */
class ListAccountsCallbackHandler implements AbstractHandler {
    private final AccountRepository accountRepository;

    @Inject
    ListAccountsCallbackHandler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Boolean handle(Request request) {
        return handleCallbackMessage(request) || handleSlashAccounts(request);
    }

    private Boolean handleSlashAccounts(Request request) {
        if (!Objects.equals(request.getMessageText(), "/accounts"))
            return false;

        sendMessageWithAccounts(request, false, false);
        return true;
    }

    private Boolean handleCallbackMessage(Request request) {
        if (!(request.getCallbackMessage().orElse(null) instanceof ListAccountsCallback cm))
            return false;

        sendMessageWithAccounts(request, cm.isIncludeHidden(), true);
        return true;
    }

    private void sendMessageWithAccounts(Request request, boolean includeHidden, boolean edit) {
        var text = ICON_ACCOUNT + " Выберите счёт для редактирования:";
        var buttons = accountRepository
            .filter(request.getTelegramUser())
            .stream()
            .filter(account -> account.isHidden() == includeHidden)
            .map(account -> new ShowAccountDetailsCallback(account.getId()).asAccountButton(account))
            .toList();

        var keyboard = KeyboardUtils.createMarkupWithFixedColumns(buttons, 2);

        var visibleAccountsListButton = new ListAccountsCallback(false).asButton("Видимые счета");
        var hiddenAccountsListButton = new ListAccountsCallback(true).asButton("Скрытые счета");

        keyboard.addRow(
            includeHidden ? visibleAccountsListButton : hiddenAccountsListButton,
            ViewAssetsCallback.firstPage().asButton("Активы")
        );

        if (edit) {
            request.editMessage(text, keyboard);
        } else {
            request.sendMessage(text, keyboard);
        }
    }
}
