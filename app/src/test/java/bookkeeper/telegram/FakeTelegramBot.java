package bookkeeper.telegram;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Getter;
import org.apache.commons.lang3.NotImplementedException;

import java.util.LinkedList;
import java.util.List;

class FakeTelegramBot extends TelegramBot {
    @Getter
    private final List<SendMessage> sentMessages = new LinkedList<>();

    FakeTelegramBot() {
        super("fake_apikey");
    }

    @Override
    public <T extends BaseRequest<T, R>, R extends BaseResponse> R execute(BaseRequest<T, R> request) {
        if (request instanceof SendMessage sm) {
            sentMessages.add(sm);
            //noinspection unchecked
            return (R) BotUtils.fromJson("{}", SendResponse.class);
        }
        throw new NotImplementedException();
    }
}
