package bookkeeper.telegram.scenario.viewAssets;

import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.KeyboardUtils;
import bookkeeper.telegram.shared.Request;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

import javax.inject.Inject;
import java.util.ArrayList;
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

        sendMessageWithAssets(request, 0, 0,5, false);
        return true;
    }

    private Boolean handleCallbackMessage(Request request) {
        if (!(request.getCallbackMessage().orElse(null) instanceof ViewAssetsWithOffsetCallback cm))
            return false;

        sendMessageWithAssets(request, cm.getMonthOffset(), cm.getPage(), cm.getPageSize(), true);
        return true;
    }

    private void sendMessageWithAssets(Request request, int monthOffset, int page, int pageSize, boolean edit) {
        var user = request.getTelegramUser();
        var message = assetsResponseFactory.getTotalAssets(user, monthOffset, page, pageSize);

        var prevMonthButton = new ViewAssetsWithOffsetCallback(monthOffset - 1, page, pageSize).asPrevMonthButton(monthOffset - 1);
        var nextMonthButton = new ViewAssetsWithOffsetCallback(monthOffset + 1, page, pageSize).asNextMonthButton(monthOffset + 1);
        var prevPageButton = new ViewAssetsWithOffsetCallback(monthOffset, page - 1, pageSize).asButton("<");
        var nextPageButton = new ViewAssetsWithOffsetCallback(monthOffset, page + 1, pageSize).asButton(">");

        var buttons = new ArrayList<InlineKeyboardButton>();

        if (page >= 1)
            buttons.add(prevPageButton);
        if (!message.endsWith(String.format(" %s / %s", page + 1, page + 1)))
            buttons.add(nextPageButton);

        buttons.add(prevMonthButton);
        buttons.add(nextMonthButton);

        var keyboard = KeyboardUtils.createMarkupWithFixedColumns(buttons, 4);

        if (edit) {
            request.editMessage(message, keyboard);
        }
        else {
            request.sendMessage(message, keyboard);
        }
    }

}
