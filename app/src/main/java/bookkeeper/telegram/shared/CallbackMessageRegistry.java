package bookkeeper.telegram.shared;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import redis.clients.jedis.JedisPool;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.*;
import java.text.ParseException;
import java.util.Base64;
import java.util.Optional;

class CallbackMessageRegistry {
    private final StringShortener shortener;

    @Inject
    CallbackMessageRegistry(JedisPool jedisPool) {
        this.shortener = new StringShortener(55, jedisPool);
    }

    static InlineKeyboardButton createButton(CallbackMessage message, String text) {
        return new InlineKeyboardButton(text).callbackData(serialize(message));
    }

    private void prepareButton(InlineKeyboardButton button) {
        button.callbackData(shortener.shrink(button.callbackData()));
    }

    void prepareKeyboard(InlineKeyboardMarkup keyboard) {
        for (var row : keyboard.inlineKeyboard()) {
            for (var button : row) {
                prepareButton(button);
            }
        }
    }

    Optional<CallbackMessage> getCallbackMessage(@Nullable CallbackQuery callbackQuery) {
        if (callbackQuery == null)
            return Optional.empty();

        return getCallbackMessage(callbackQuery.data());
    }

    Optional<CallbackMessage> getCallbackMessage(String callbackData) {
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
