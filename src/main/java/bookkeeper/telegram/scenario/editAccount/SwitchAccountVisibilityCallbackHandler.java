package bookkeeper.telegram.scenario.editAccount;

import bookkeeper.dao.repository.AccountRepository;
import bookkeeper.exception.AccountNotFound;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;

import javax.inject.Inject;

import static bookkeeper.telegram.scenario.editAccount.AccountResponseFactory.getMessageKeyboard;

/**
 * Scenario: User manages account visibility.
 */
class SwitchAccountVisibilityCallbackHandler implements AbstractHandler {
    private final AccountRepository accountRepository;
    private final AccountResponseFactory accountResponseFactory;

    @Inject
    SwitchAccountVisibilityCallbackHandler(AccountRepository accountRepository, AccountResponseFactory accountResponseFactory) {
        this.accountRepository = accountRepository;
        this.accountResponseFactory = accountResponseFactory;
    }

    public Boolean handle(Request request) throws AccountNotFound {
        return switchVisibility(request);
    }

    private Boolean switchVisibility(Request request) throws AccountNotFound {
        if (!(request.getCallbackMessage().orElse(null) instanceof SwitchAccountVisibilityCallback cm))
            return false;

        var account = accountRepository.get(cm.getAccountId()).orElseThrow(() -> new AccountNotFound(cm.getAccountId()));

        account.setHidden(cm.isHidden());
        request.editMessage(accountResponseFactory.getMessageText(account), getMessageKeyboard(account));

        return true;
    }
}
