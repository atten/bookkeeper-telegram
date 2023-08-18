package bookkeeper.services.repositories;

import bookkeeper.entities.TelegramUser;
import com.pengrad.telegrambot.model.User;
import jakarta.persistence.EntityManager;

import java.time.Instant;

public class TelegramUserRepository {
    private final EntityManager manager;

    public TelegramUserRepository(EntityManager manager) {
        this.manager = manager;
    }

    public TelegramUser getOrCreate(User user) {
        var telegramUser = manager.find(TelegramUser.class, user.id());

        if (telegramUser == null)
            telegramUser = newUserFactory(user);

        return manager.merge(telegramUser);
    }

    public void updateLastAccess(TelegramUser telegramUser) {
        telegramUser.setLastAccess(Instant.now());
    }

    private TelegramUser newUserFactory(User user) {
        var telegramUser = new TelegramUser();
        telegramUser.setTelegramId(user.id());
        telegramUser.setUsername(user.username());
        telegramUser.setFirstAccess(Instant.now());
        telegramUser.setLastAccess(Instant.now());
        return telegramUser;
    }

}
