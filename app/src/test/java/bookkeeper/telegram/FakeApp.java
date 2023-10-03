package bookkeeper.telegram;

import com.pengrad.telegrambot.model.User;
import jakarta.persistence.EntityManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FakeApp {
    private static final FakeTelegramContainer container = DaggerFakeTelegramContainer.builder().build();

    public static FakeSession session() {
        clearDatabase();
        var telegramUser = new User(123L);
        return new FakeSession(telegramUser, container.bot(), container.fakeTelegramBot());
    }

    private static void clearDatabase() {
        clearDatabase(container.entityManager());
    }

    private static void clearDatabase(EntityManager entityManager) {
        String sqlPath = "clear_database.sql";
        String sql;
        try {
            var resourcePath = Path.of(Objects.requireNonNull(Config.class.getResource("/" + sqlPath)).toURI());
            sql = Files.readString(resourcePath);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

        var query = entityManager.createNativeQuery(sql);
        entityManager.getTransaction().begin();
        query.executeUpdate();
        entityManager.getTransaction().commit();
    }
}
