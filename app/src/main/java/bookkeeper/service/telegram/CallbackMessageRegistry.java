package bookkeeper.service.telegram;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import redis.clients.jedis.JedisPool;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.*;
import java.text.ParseException;
import java.util.*;

class CallbackMessageRegistry {
    private final StringShortener shortener;
    private final Map<String, CallbackMessage> runtimeMessageCache;

    @Inject
    CallbackMessageRegistry(JedisPool jedisPool) {
        this.shortener = new StringShortener(55, jedisPool);
        this.runtimeMessageCache = new HashMap<>();
    }

    static InlineKeyboardButton createButton(CallbackMessage message, String text) {
        return new InlineKeyboardButton(text).callbackData(serialize(message));
    }

    void prepareKeyboardBeforeSend(InlineKeyboardMarkup keyboard) {
        KeyboardUtils.getButtons(keyboard).forEach(this::prepareButtonBeforeSend);
    }

    Optional<CallbackMessage> getCallbackMessage(@Nullable CallbackQuery callbackQuery) {
        if (callbackQuery == null)
            return Optional.empty();

        return getCallbackMessage(callbackQuery.data());
    }

    Optional<CallbackMessage> getCallbackMessage(String callbackData) {
        if (runtimeMessageCache.containsKey(callbackData))
            return Optional.of(runtimeMessageCache.get(callbackData));

        var fullCallbackData = shortener.unshrink(callbackData);

        try {
            var callbackMessage = deserialize(fullCallbackData);
            compactMessageCache();
            runtimeMessageCache.put(callbackData, callbackMessage);
            return Optional.of(callbackMessage);
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

    private void prepareButtonBeforeSend(InlineKeyboardButton button) {
        button.callbackData(shortener.shrink(button.callbackData()));
    }

    private void compactMessageCache() {
        var maxSize = 1000;
        if (runtimeMessageCache.size() > maxSize) {
            runtimeMessageCache.clear();
        }
    }
}
