package bookkeeper.telegram.scenario.viewAssets;

import bookkeeper.telegram.scenario.editAccount.ListAccountsCallback;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.KeyboardUtils;
import bookkeeper.service.telegram.Request;
import bookkeeper.service.telegram.StringUtils;
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

        sendMessageWithAssets(request, ViewAssetsCallback.firstPage(), false);
        return true;
    }

    private Boolean handleCallbackMessage(Request request) {
        if (!(request.getCallbackMessage().orElse(null) instanceof ViewAssetsCallback cm))
            return false;

        sendMessageWithAssets(request, cm,true);
        return true;
    }

    private void sendMessageWithAssets(Request request, ViewAssetsCallback cm, boolean edit) {
        var user = request.getTelegramUser();
        var page = cm.getPage();
        var pageSize = cm.getPageSize();
        var monthOffset = cm.getMonthOffset();
        var message = assetsResponseFactory.getTotalAssets(user, monthOffset, page, pageSize);

        var prevMonthButton = new ViewAssetsCallback(monthOffset - 1, page, pageSize).asPrevMonthButton(monthOffset - 1);
        var nextMonthButton = new ViewAssetsCallback(monthOffset + 1, page, pageSize).asNextMonthButton(monthOffset + 1);
        var prevPageButton = new ViewAssetsCallback(monthOffset, page - 1, pageSize).asButton("<");
        var nextPageButton = new ViewAssetsCallback(monthOffset, page + 1, pageSize).asButton(">");
        var editAccountsButton = new ListAccountsCallback().asButton(StringUtils.ICON_ACCOUNT + "Счета");

        var buttons = new ArrayList<InlineKeyboardButton>();

        if (page >= 1)
            buttons.add(prevPageButton);
        if (!message.endsWith(String.format(" %s / %s", page + 1, page + 1)))
            buttons.add(nextPageButton);

        buttons.add(prevMonthButton);
        buttons.add(nextMonthButton);
        buttons.add(editAccountsButton);

        var keyboard = KeyboardUtils.createMarkupWithFixedColumns(buttons, 4);

        if (edit) {
            request.editMessage(message, keyboard);
        }
        else {
            request.sendMessage(message, keyboard);
        }
    }

}
