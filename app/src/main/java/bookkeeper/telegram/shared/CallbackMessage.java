package bookkeeper.telegram.shared;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

import java.io.Serializable;

public abstract class CallbackMessage implements Serializable {

    public InlineKeyboardButton asButton(String text) {
        return CallbackMessageRegistry.createButton(this, text);
    }

}
