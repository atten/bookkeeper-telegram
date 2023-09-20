package bookkeeper.service.registry;

import bookkeeper.telegram.Config;
import bookkeeper.telegram.shared.CallbackMessage;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

import javax.annotation.Nullable;
import java.io.*;
import java.text.ParseException;
import java.util.Base64;
import java.util.Optional;

public class CallbackMessageRegistry {
    private static final StringShortener shortener = new StringShortener(55, Config.redisPool());

    public static InlineKeyboardButton createButton(CallbackMessage message, String text) {
        return new InlineKeyboardButton(text).callbackData(shortener.shrink(serialize(message)));
    }

    public static Optional<CallbackMessage> getCallbackMessage(@Nullable CallbackQuery callbackQuery) {
        if (callbackQuery == null)
            return Optional.empty();

        return getCallbackMessage(callbackQuery.data());
    }

    public static Optional<CallbackMessage> getCallbackMessage(String callbackData) {
        callbackData = shortener.unshrink(callbackData);

        try {
            return Optional.of(deserialize(callbackData));
        } catch (ParseException e) {
            return Optional.empty();
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
        try {
            var input = new ByteArrayInputStream(Base64.getDecoder().decode(callbackData));
            return (CallbackMessage) new ObjectInputStream(input).readObject();
        } catch (IOException | ClassNotFoundException | IllegalArgumentException e) {
            throw new ParseException(callbackData, 0);
        }
    }
}
