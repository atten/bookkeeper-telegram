package bookkeeper.telegram;

import bookkeeper.dao.repository.TelegramCallbackMessageRepository;
import bookkeeper.service.ApplicationConfiguration;
import bookkeeper.service.telegram.StringShortenerCache;
import bookkeeper.service.telegram.StringShortenerCacheDb;
import bookkeeper.service.telegram.StringShortenerCacheRedis;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.DeleteWebhook;
import com.pengrad.telegrambot.request.GetWebhookInfo;
import com.pengrad.telegrambot.request.SetWebhook;
import com.sun.net.httpserver.HttpServer;
import dagger.Module;
import dagger.Provides;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.JDBCException;
import redis.clients.jedis.JedisPool;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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
    private EntityManager entityManager;

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
        if (entityManager == null) {
            entityManager = Persistence.createEntityManagerFactory("default", dataSourceConfig).createEntityManager();
            migrate(entityManager);
        }
        return entityManager;
    }

    @Provides
    @Singleton
    StringShortenerCache stringShortenerCache() {
        var path = properties.getProperty("jedis.redis.path");
        if (path != null && !path.isEmpty() && !path.equals("null")) {
            log.info("Current cache: redis");
            return new StringShortenerCacheRedis(new JedisPool(path));
        }
        log.info("Current cache: db");
        return new StringShortenerCacheDb(new TelegramCallbackMessageRepository(entityManager));
    }

    @Provides
    @Singleton
    Optional<HttpServer> webhookServer() {
        var bot = telegramBot();
        var webhookResult = bot.execute(new GetWebhookInfo());
        if (!webhookResult.isOk())
            throw new RuntimeException(webhookResult.toString());

        var webhookUrl = System.getenv("WEBHOOK_URL");
        var port = System.getenv("PORT"); // pre-defined variable name in serverless container
        var webhookInfo = webhookResult.webhookInfo();

        if (webhookInfo.url() != null && !webhookInfo.url().isEmpty()) {
            log.info("Current webhook: {}", webhookInfo);

            if (!Objects.equals(webhookUrl, webhookInfo.url())) {
                log.info("Remove current webhook...");
                bot.execute(new DeleteWebhook().dropPendingUpdates(false));
            }
        }

        if (webhookUrl == null)
            return Optional.empty();

        if (port == null)
            port = "80";

        bot.removeGetUpdatesListener();

        if (!webhookInfo.url().equals(webhookUrl)) {
            SetWebhook request = new SetWebhook().url(webhookUrl);
            var result = bot.execute(request);
            var resultVerbose = result.description() != null ? result.description() : "OK";
            log.info("Set webhook: {}... {}", request.toWebhookResponse(), resultVerbose);

            if (!result.isOk())
                throw new RuntimeException(result.toString());
        }

        try {
            var server = HttpServer.create(new InetSocketAddress(InetAddress.getByName("0.0.0.0"), Integer.parseInt(port)), 0);
            return Optional.of(server);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

        log.info("Run {}...", sqlPath);
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
