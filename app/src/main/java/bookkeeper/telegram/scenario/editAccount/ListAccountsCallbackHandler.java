package bookkeeper.telegram.scenario.editAccount;

import bookkeeper.service.repository.AccountRepository;
import bookkeeper.telegram.scenario.viewAssets.ViewAssetsCallback;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.KeyboardUtils;
import bookkeeper.telegram.shared.Request;

import javax.inject.Inject;
import java.util.Objects;

import static bookkeeper.telegram.shared.StringUtils.ICON_ACCOUNT;

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

        sendMessageWithAccounts(request, false);
        return true;
    }

    private Boolean handleCallbackMessage(Request request) {
        if (!(request.getCallbackMessage().orElse(null) instanceof ListAccountsCallback))
            return false;

        sendMessageWithAccounts(request, true);
        return true;
    }

    private void sendMessageWithAccounts(Request request, boolean edit) {
        var text = ICON_ACCOUNT + " Выберите счёт для редактирования:";
        var buttons = accountRepository
            .filter(request.getTelegramUser())
            .stream()
            .map(account -> new ShowAccountDetailsCallback(account.getId()).asAccountButton(account))
            .toList();

        var keyboard = KeyboardUtils.createMarkupWithFixedColumns(buttons, 2);

        keyboard.addRow(ViewAssetsCallback.firstPage().asButton("Активы"));

        if (edit) {
            request.editMessage(text, keyboard);
        } else {
            request.sendMessage(text, keyboard);
        }
    }
}
