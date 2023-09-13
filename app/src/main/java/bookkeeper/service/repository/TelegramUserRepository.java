package bookkeeper.service.repository;

import bookkeeper.entity.TelegramUser;
import com.pengrad.telegrambot.model.User;
import dagger.Reusable;
import jakarta.persistence.EntityManager;

import javax.inject.Inject;
import java.time.Instant;

@Reusable
public class TelegramUserRepository {
    private final EntityManager manager;

    @Inject
    public TelegramUserRepository(EntityManager manager) {
        this.manager = manager;
    }

    public TelegramUser getOrCreate(User user) {
        var telegramUser = manager.find(TelegramUser.class, user.id());

        if (telegramUser == null)
            telegramUser = newUserFactory(user);

        // update attributes:
        // set lang only if it's different from default
        var updateLanguageCode = user.languageCode();
        if (!updateLanguageCode.equals("en"))
            telegramUser.setLanguageCode(updateLanguageCode);

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
