package bookkeeper.telegram.callbacks;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

import java.text.ParseException;

public abstract class CallbackMessage {
    abstract CallbackMessage parse(String message) throws ParseException;

    public InlineKeyboardButton asButton(String text) {
        return new InlineKeyboardButton(text).callbackData(toString());
    }
}
