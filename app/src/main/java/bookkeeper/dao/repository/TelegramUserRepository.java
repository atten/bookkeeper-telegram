package bookkeeper.dao.repository;

import bookkeeper.dao.entity.TelegramUser;
import com.pengrad.telegrambot.model.User;
import dagger.Reusable;
import jakarta.persistence.EntityManager;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Optional;

@Reusable
public class TelegramUserRepository {
    private final EntityManager manager;

    @Inject
    TelegramUserRepository(EntityManager manager) {
        this.manager = manager;
    }

    public TelegramUser getOrCreate(User user) {
        var telegramUser = Optional.ofNullable(
            manager.find(TelegramUser.class, user.id())
        ).orElseGet(
            () -> newUserFactory(user)
        );

        // update attributes:
        // set lang only if it's different from default
        var updateLanguageCode = user.languageCode();
        if (updateLanguageCode != null && !updateLanguageCode.equals("en"))
            telegramUser.setLanguageCode(updateLanguageCode);

        return manager.merge(telegramUser);
    }

    public void updateLastAccess(TelegramUser telegramUser) {
        telegramUser.setLastAccess(LocalDate.now());
    }

    private TelegramUser newUserFactory(User user) {
        var telegramUser = new TelegramUser();
        telegramUser.setTelegramId(user.id());
        telegramUser.setUsername(user.username());
        telegramUser.setFirstAccess(LocalDate.now());
        telegramUser.setLastAccess(LocalDate.now());
        telegramUser.setLanguageCode("en");
        return telegramUser;
    }

}
