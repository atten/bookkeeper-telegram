package bookkeeper.telegram;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.utility.BotUtils;

import java.util.LinkedHashMap;
import java.util.Map;

class UpdateBuilder {
    private static final Gson gson = new GsonBuilder()
        // for proper serialization (callbackData -> callback_data)
        .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create();

    private User user;
    private final Map<String, Object> messageMap = createMap();
    private final Map<String, Object> callbackQueryMap = createMap();

    UpdateBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    UpdateBuilder setMessage(String text) {
        var chatMap = createMap();

        chatMap.put("id", 1);

        messageMap.put("message_id", 100);
        messageMap.put("chat", chatMap);
        messageMap.put("from", user);
        messageMap.put("text", text);
        return this;
    }

    UpdateBuilder setCallbackQuery(String data) {
        var chatMap = createMap();
        var messageMap = createMap();

        chatMap.put("id", 1);

        messageMap.put("message_id", 100);
        messageMap.put("chat", chatMap);

        callbackQueryMap.put("from", user);
        callbackQueryMap.put("data", data);
        callbackQueryMap.put("message", messageMap);
        return this;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    UpdateBuilder setReplyTo(BaseRequest request) {
        var message = fromMap(request.getParameters(), Message.class);
        this.messageMap.put("reply_to_message", message);
        return this;
    }

    Update build() {
        var map = createMap();

        if (!messageMap.isEmpty()) {
            var message = fromMap(messageMap, Message.class);
            map.put("message", message);
        }

        if (!callbackQueryMap.isEmpty()) {
            var callbackQuery = fromMap(callbackQueryMap, CallbackQuery.class);
            map.put("callback_query", callbackQuery);
        }

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
