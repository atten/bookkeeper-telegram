package bookkeeper.telegram.scenario.addTransfer;

import bookkeeper.dao.repository.AccountRepository;
import bookkeeper.dao.repository.AccountTransferRepository;
import bookkeeper.exception.AccountTransferNotFound;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;

import javax.inject.Inject;

import static bookkeeper.service.telegram.StringUtils.strikeoutMessage;


/**
 * Scenario: user cancels transfer addition.
 */
class RemoveTransferCallbackHandler implements AbstractHandler {
    private final AccountTransferRepository transferRepository;
    private final AddTransferResponseFactory responseFactory;

    @Inject
    RemoveTransferCallbackHandler(AccountRepository accountRepository, AccountTransferRepository transferRepository) {
        this.transferRepository = transferRepository;
        this.responseFactory = new AddTransferResponseFactory(accountRepository);
    }

    /**
     * Handle "Cancel" click: delete given transfer.
     */
    public Boolean handle(Request request) throws AccountTransferNotFound {
        if (!(request.getCallbackMessage().orElse(null) instanceof RemoveTransferCallback cm))
            return false;

        var transfer = transferRepository.get(cm.getTransferId()).orElseThrow(() -> new AccountTransferNotFound(cm.getTransferId()));
        transferRepository.remove(transfer);
        request.editMessage(strikeoutMessage(responseFactory.getDescriptionForTransferCreated(transfer)));
        return true;
    }
}
