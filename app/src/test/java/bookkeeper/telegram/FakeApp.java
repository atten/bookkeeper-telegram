package bookkeeper.telegram;

import com.pengrad.telegrambot.model.User;

public class FakeApp {
    private static final FakeTelegramContainer container = DaggerFakeTelegramContainer.builder().build();

    public static FakeSession session() {
        var telegramUser = new User(123L);
        return new FakeSession(telegramUser, container.bot(), container.fakeTelegramBot());
    }
}
