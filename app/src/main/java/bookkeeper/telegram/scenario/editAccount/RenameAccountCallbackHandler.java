package bookkeeper.telegram.scenario.editAccount;

import bookkeeper.service.repository.AccountRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.Request;
import bookkeeper.exception.AccountNotFound;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import javax.inject.Inject;

import static bookkeeper.telegram.scenario.editAccount.AccountResponseFactory.getMessageKeyboard;
import static bookkeeper.telegram.scenario.editAccount.AccountResponseFactory.getMessageText;

/**
 * Scenario: User renames account.
 */
class RenameAccountCallbackHandler implements AbstractHandler {
    private final AccountRepository accountRepository;

    @Inject
    RenameAccountCallbackHandler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Boolean handle(Request request) throws AccountNotFound {
        return promptRename(request) || handleRename(request);
    }

    private Boolean promptRename(Request request) throws AccountNotFound {
        if (!(request.getCallbackMessage().orElse(null) instanceof RenameAccountCallback cm))
            return false;

        var account = accountRepository.get(cm.getAccountId()).orElseThrow(() -> new AccountNotFound(cm.getAccountId()));
        var kb = new InlineKeyboardMarkup(new ShowAccountDetailsCallback(account.getId()).asButton("Назад"));
        request.editMessage(String.format("Введите *в ответе* новое имя для %s:", account.getName()), kb);

        return true;
    }

    private Boolean handleRename(Request request) throws AccountNotFound {
        if (!(request.getCallbackMessageFromReply(0).orElse(null) instanceof ShowAccountDetailsCallback cm))
            return false;

        var replyToMessage = request.getReplyToMessage().orElseThrow();
        if (!replyToMessage.text().contains("новое имя"))
            return false;

        var account = accountRepository.get(cm.getAccountId()).orElseThrow(() -> new AccountNotFound(cm.getAccountId()));

        account.setName(request.getMessageText());

        request.editMessage(getMessageText(account), getMessageKeyboard(account), replyToMessage.messageId());
        request.deleteMessage();
        return true;
    }
}
