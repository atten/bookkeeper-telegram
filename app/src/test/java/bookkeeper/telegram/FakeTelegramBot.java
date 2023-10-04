package bookkeeper.telegram;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Getter;

import java.util.LinkedList;

class FakeTelegramBot extends TelegramBot {
    @Getter
    private final LinkedList<BaseRequest> sentMessages = new LinkedList<>();

    FakeTelegramBot() {
        super("fake_apikey");
    }

    @Override
    public <T extends BaseRequest<T, R>, R extends BaseResponse> R execute(BaseRequest<T, R> request) {
        sentMessages.add(request);
        return (R) BotUtils.fromJson("{}", SendResponse.class);
    }
}
