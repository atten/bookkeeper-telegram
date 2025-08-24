package bookkeeper.telegram.scenario.editAccount;

import bookkeeper.dao.repository.AccountRepository;
import bookkeeper.exception.AccountNotFound;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import javax.inject.Inject;

import static bookkeeper.telegram.scenario.editAccount.AccountResponseFactory.getMessageKeyboard;
import static bookkeeper.telegram.scenario.editAccount.AccountResponseFactory.getMessageText;

/**
 * Scenario: User sets account notes.
 */
class SetAccountNotesCallbackHandler implements AbstractHandler {
    private final AccountRepository accountRepository;

    @Inject
    SetAccountNotesCallbackHandler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Boolean handle(Request request) throws AccountNotFound {
        return promptEdit(request) || handleEdit(request);
    }

    private Boolean promptEdit(Request request) throws AccountNotFound {
        if (!(request.getCallbackMessage().orElse(null) instanceof SetAccountNotesCallback cm))
            return false;

        var account = accountRepository.get(cm.getAccountId()).orElseThrow(() -> new AccountNotFound(cm.getAccountId()));
        var kb = new InlineKeyboardMarkup(new ShowAccountDetailsCallback(account.getId()).asButton("Назад"));
        request.editMessage("Введите *в ответе* заметки для %s:".formatted(account.getName()), kb);

        return true;
    }

    private Boolean handleEdit(Request request) throws AccountNotFound {
        if (!(request.getCallbackMessageFromReply().orElse(null) instanceof ShowAccountDetailsCallback cm))
            return false;

        var replyToMessage = request.getReplyToMessage().orElseThrow();
        if (!replyToMessage.text().contains("заметки"))
            return false;

        var account = accountRepository.get(cm.getAccountId()).orElseThrow(() -> new AccountNotFound(cm.getAccountId()));

        account.setNotes(request.getMessageText());

        request.editMessage(getMessageText(account), getMessageKeyboard(account), replyToMessage.messageId());
        request.deleteMessage();
        return true;
    }
}
