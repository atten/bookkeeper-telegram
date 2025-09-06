package bookkeeper.telegram.scenario.checkResidues;

import bookkeeper.exception.HandlerInterruptException;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Scenario: User compares account residue written in latest transaction record with calculated account residue.
 */
public class CheckResiduesMessageHandler implements AbstractHandler {
    private final CheckResiduesResponseFactory responseFactory;

    @Inject
    public CheckResiduesMessageHandler(CheckResiduesResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
    }

    @Override
    public Boolean handle(Request request) throws HandlerInterruptException {
        if (!Objects.equals(request.getMessageText(), "/check_residues"))
            return false;

        request.sendMessage(responseFactory.getResiduesCheckResult(request.getTelegramUser()));
        return true;
    }
}
