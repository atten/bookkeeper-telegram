package bookkeeper.telegram;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Config {
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

    public static JedisPool redisPool() {
        var path = applicationProperties().getProperty("jedis.redis.path");
        return new JedisPool(path);
    }

    static Optional<Integer> telegramUserIdToNotify() {
        var userId = System.getenv("NOTIFY_TELEGRAM_USER_ID");
        if (userId == null)
            return Optional.empty();
        return Optional.of(Integer.parseInt(userId));
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

    /**
     * Build JDBC config from env variables (if not set, defaults will be taken from META-INF/persistence.xml).
     */
    private static Map<String, String> dataSourceConfig() {
        Map<String, String> result = new HashMap<>();

        List.of(
            "jakarta.persistence.jdbc.url",
            "jakarta.persistence.jdbc.user",
            "jakarta.persistence.jdbc.password"
        ).forEach(s -> {
            var value = System.getenv(s);
            if (value != null && !value.isEmpty())
                result.put(s, value);
        } );

        return result;
    }

    private static Properties applicationProperties() {
        var p = new Properties();
        var resource = Config.class.getResource("/application.properties");
        Objects.requireNonNull(resource);
        try {
            p.load(new FileInputStream(resource.getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // update values from env variables
        for (var key : p.keySet()) {
            var strKey = (String) key;
            var value = System.getenv(strKey);
            if (value != null && !value.isEmpty())
                p.setProperty(strKey, value);
        }

        return p;
    }
}
