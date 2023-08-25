package bookkeeper.telegram.shared;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.text.ParseException;
import java.util.Base64;

public class CallbackMessageRegistry {
    private static final StringShortener shortener = new StringShortener(55);

    static InlineKeyboardButton createButton(CallbackMessage message, String text) {
        return new InlineKeyboardButton(text).callbackData(shortener.shrink(serialize(message)));
    }

    @Nullable
    public static CallbackMessage getCallbackMessage(Update update) {
        if (update.callbackQuery() == null)
            return null;

        var callbackData = shortener.unshrink(update.callbackQuery().data());

        try {
            return deserialize(callbackData);
        } catch (ParseException e) {
            return null;
        }
    }

    private static String serialize(CallbackMessage message) {
        var output = new ByteArrayOutputStream();
        try {
            new ObjectOutputStream(output).writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(output.toByteArray());
    }

    private static CallbackMessage deserialize(String callbackData) throws ParseException {
        var input = new ByteArrayInputStream(Base64.getDecoder().decode(callbackData));
        try {
            return (CallbackMessage) new ObjectInputStream(input).readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new ParseException(callbackData, 0);
        }
    }
}
