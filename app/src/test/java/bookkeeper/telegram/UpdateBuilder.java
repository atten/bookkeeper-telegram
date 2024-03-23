package bookkeeper.telegram;

import com.google.gson.Gson;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;

import java.util.LinkedHashMap;
import java.util.Map;

class UpdateBuilder {
    private static final Gson gson = new Gson();

    private User user;
    private Message message;
    private CallbackQuery callbackQuery;

    UpdateBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    UpdateBuilder setMessage(String text) {
        var map = createMap();
        var chatMap = createMap();

        chatMap.put("id", 1);

        map.put("message_id", 100);
        map.put("chat", chatMap);
        map.put("from", user);
        map.put("text", text);
        this.message = fromMap(map, Message.class);
        return this;
    }

    UpdateBuilder setCallbackQuery(String data) {
        var map = createMap();
        var chatMap = createMap();
        var messageMap = createMap();

        chatMap.put("id", 1);

        messageMap.put("message_id", 100);
        messageMap.put("chat", chatMap);

        map.put("from", user);
        map.put("data", data);
        map.put("message", messageMap);
        this.callbackQuery = fromMap(map, CallbackQuery.class);
        return this;
    }

    Update build() {
        var map = createMap();
        map.put("message", message);
        map.put("callback_query", callbackQuery);
        return fromMap(map, Update.class);
    }

    private static Map<String, Object> createMap() {
        return new LinkedHashMap<>();
    }

    private static <R> R fromMap(Map<String, Object> map, Class<R> resClass) {
        var json = gson.toJson(map);
        return BotUtils.fromJson(json, resClass);
    }
}
