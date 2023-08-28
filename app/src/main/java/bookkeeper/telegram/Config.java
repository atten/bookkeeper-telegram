package bookkeeper.telegram;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import org.hibernate.HibernateException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    static String botToken() {
        return System.getenv("BOT_TOKEN");
    }

    /**
     There's only one instance of database writer (the bot itself), so we can use a single persistence context throughout runtime.
     */
    static EntityManager entityManager() {
        var em = Persistence.createEntityManagerFactory("default", dataSourceConfig()).createEntityManager();
        migrate(em);
        return em;
    }

    @Nullable
    static Integer notifyTelegramUserId() {
        var userId = System.getenv("NOTIFY_TELEGRAM_USER_ID");
        if (userId == null)
            return null;
        return Integer.parseInt(userId);
    }

    private static void migrate(EntityManager entityManager) {
        String sql;
        try {
            var path = Path.of(Objects.requireNonNull(Config.class.getResource("/init_database.sql")).toURI());
            sql = Files.readString(path);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

        if (sql.isEmpty())
            return;

        var query = entityManager.createNativeQuery(sql);
        try {
            query.getSingleResult();
        } catch (HibernateException e) {
            logger.warn(e.toString());
        }
    }

    private static Map<String, String> dataSourceConfig() {
        Map<String, String> result = new HashMap<>();

        List.of(
            "jakarta.persistence.jdbc.url",
            "jakarta.persistence.jdbc.user",
            "jakarta.persistence.jdbc.password"
        ).forEach(s -> {
            var value = System.getenv(s);
            if (!Objects.equals(value, ""))
                result.put(s, value);
        } );

        return result;
    }
}
