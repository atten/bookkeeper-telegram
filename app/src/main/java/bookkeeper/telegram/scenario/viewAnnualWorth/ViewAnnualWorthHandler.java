package bookkeeper.telegram.scenario.viewAnnualWorth;

import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.Request;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Scenario: User requests annual month-to-month NAV chart.
 */
class ViewAnnualWorthHandler implements AbstractHandler {
    private final AnnualWorthResponseFactory responseFactory;
    @Inject
    ViewAnnualWorthHandler(AnnualWorthResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
    }

    @Override
    public Boolean handle(Request request) {
        if (!Objects.equals(request.getMessageText(), "/annual"))
            return false;

        request.sendMessage(responseFactory.getAnnualWorth(request.getTelegramUser()));
        return true;
    }
}
