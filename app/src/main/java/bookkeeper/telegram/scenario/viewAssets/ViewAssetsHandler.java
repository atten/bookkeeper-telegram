package bookkeeper.telegram.scenario.viewAssets;

import bookkeeper.service.registry.CallbackMessageRegistry;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.Request;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import javax.inject.Inject;
import java.util.Objects;


/**
 * Scenario: user requests total assets.
 */
class ViewAssetsHandler implements AbstractHandler {
    private final AssetsResponseFactory assetsResponseFactory;

    @Inject
    ViewAssetsHandler(AssetsResponseFactory assetsResponseFactory) {
        this.assetsResponseFactory = assetsResponseFactory;
    }

    /**
     * Display total assets overview
     */
    public Boolean handle(Request request) {
        return handleCallbackMessage(request) || handleSlashAssets(request);
    }

    private Boolean handleSlashAssets(Request request) {
        if (!Objects.equals(request.getMessageText(), "/assets"))
            return false;

        sendMessageWithAssets(request, 0, false);
        return true;
    }

    private Boolean handleCallbackMessage(Request request) {
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(request.getUpdate());
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof ViewAssetsWithOffsetCallback cm))
            return false;

        sendMessageWithAssets(request, cm.getMonthOffset(), true);
        return true;
    }

    private void sendMessageWithAssets(Request request, int monthOffset, boolean edit) {
        var user = request.getTelegramUser();
        var message = assetsResponseFactory.getTotalAssets(user, monthOffset);
        var keyboard = new InlineKeyboardMarkup().addRow(
                new ViewAssetsWithOffsetCallback(monthOffset - 1).asPrevMonthButton(monthOffset - 1),
                new ViewAssetsWithOffsetCallback(monthOffset + 1).asNextMonthButton(monthOffset + 1)
        );

        if (edit)
            request.editMessage(message, keyboard);
        else
            request.sendMessage(message, keyboard);
    }

}
