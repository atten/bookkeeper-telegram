package bookkeeper.telegram.scenario.editAccount;

import bookkeeper.dao.repository.AccountRepository;
import bookkeeper.exception.AccountNotFound;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import javax.inject.Inject;

import static bookkeeper.telegram.scenario.editAccount.AccountResponseFactory.getMessageKeyboard;

/**
 * Scenario: User renames account.
 */
class RenameAccountCallbackHandler implements AbstractHandler {
    private final AccountRepository accountRepository;
    private final AccountResponseFactory accountResponseFactory;

    @Inject
    RenameAccountCallbackHandler(AccountRepository accountRepository, AccountResponseFactory accountResponseFactory) {
        this.accountRepository = accountRepository;
        this.accountResponseFactory = accountResponseFactory;
    }

    public Boolean handle(Request request) throws AccountNotFound {
        return promptRename(request) || handleRename(request);
    }

    private Boolean promptRename(Request request) throws AccountNotFound {
        if (!(request.getCallbackMessage().orElse(null) instanceof RenameAccountCallback cm))
            return false;

        var account = accountRepository.get(cm.getAccountId()).orElseThrow(() -> new AccountNotFound(cm.getAccountId()));
        var kb = new InlineKeyboardMarkup(new ShowAccountDetailsCallback(account.getId()).asButton("Назад"));
        request.editMessage("Введите *в ответе* новое имя для %s:".formatted(account.getName()), kb);

        return true;
    }

    private Boolean handleRename(Request request) throws AccountNotFound {
        if (!(request.getCallbackMessageFromReply().orElse(null) instanceof ShowAccountDetailsCallback cm))
            return false;

        var replyToMessage = request.getReplyToMessage().orElseThrow();
        if (!replyToMessage.text().contains("новое имя"))
            return false;

        var account = accountRepository.get(cm.getAccountId()).orElseThrow(() -> new AccountNotFound(cm.getAccountId()));

        account.setName(request.getMessageText());

        request.editMessage(accountResponseFactory.getMessageText(account), getMessageKeyboard(account), replyToMessage.messageId());
        request.deleteMessage();
        return true;
    }
}
