package bookkeeper.telegram.scenario.editAccount;

import bookkeeper.dao.repository.AccountRepository;
import bookkeeper.exception.AccountNotFound;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;

import javax.inject.Inject;

import static bookkeeper.telegram.scenario.editAccount.AccountResponseFactory.getMessageKeyboard;
import static bookkeeper.telegram.scenario.editAccount.AccountResponseFactory.getMessageText;

/**
 * Scenario: User requests account details to change attributes.
 */
class ShowAccountDetailsCallbackHandler implements AbstractHandler {
    private final AccountRepository accountRepository;

    @Inject
    ShowAccountDetailsCallbackHandler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Boolean handle(Request request) throws AccountNotFound {
        if (!(request.getCallbackMessage().orElse(null) instanceof ShowAccountDetailsCallback cm))
            return false;

        var account = accountRepository.get(cm.getAccountId()).orElseThrow(() -> new AccountNotFound(cm.getAccountId()));
        request.editMessage(getMessageText(account), getMessageKeyboard(account));
        return true;
    }
}
