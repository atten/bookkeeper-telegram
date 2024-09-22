package bookkeeper.telegram;

import bookkeeper.service.ApplicationConfiguration;
import com.pengrad.telegrambot.TelegramBot;
import dagger.Module;
import dagger.Provides;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.JDBCException;
import redis.clients.jedis.JedisPool;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

@Module
@Slf4j
class Config {
    private final Properties properties = ApplicationConfiguration.getApplicationProperties("/application.properties");
    private final Map<String, String> dataSourceConfig = ApplicationConfiguration.getConfigMap(
        "jakarta.persistence.jdbc.url",
        "jakarta.persistence.jdbc.user",
        "jakarta.persistence.jdbc.password"
    );

    @Provides
    @Singleton
    TelegramBot telegramBot() {
        return new TelegramBot(botToken());
    }

    /**
     There's only one instance of database writer (the bot itself), so we can use a single persistence context throughout runtime.
     JDBC config is taken from META-INF/persistence.xml and can be override if env variables are set (provided by dataSourceConfig).
     */
    @Provides
    @Singleton
    EntityManager entityManager() {
        var em = Persistence.createEntityManagerFactory("default", dataSourceConfig).createEntityManager();
        migrate(em);
        return em;
    }

    @Provides
    @Singleton
    JedisPool redisPool() {
        var path = properties.getProperty("jedis.redis.path");
        return new JedisPool(path);
    }

    @Provides
    @Singleton
    static Optional<Integer> telegramUserIdToNotify() {
        var userId = System.getenv("NOTIFY_TELEGRAM_USER_ID");
        if (userId == null)
            return Optional.empty();
        return Optional.of(Integer.parseInt(userId));
    }

    private void migrate(EntityManager entityManager) {
        String sqlPath = "init_database.sql";
        String sql;
        try {
            var resourcePath = Path.of(Objects.requireNonNull(Config.class.getResource("/" + sqlPath)).toURI());
            sql = Files.readString(resourcePath);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

        if (sql.isEmpty())
            return;

        log.info("Run %s...".formatted(sqlPath));
        var query = entityManager.createNativeQuery(sql);
        try {
            query.getSingleResult();
        } catch (JDBCException e) {
            log.warn(e.getCause().toString());
        }
    }

    private String botToken() {
        return System.getenv("BOT_TOKEN");
    }
}
