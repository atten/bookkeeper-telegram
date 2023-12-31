package bookkeeper.telegram;

import bookkeeper.service.ApplicationConfiguration;
import bookkeeper.service.client.CbrApiClient;
import bookkeeper.service.client.MockedCbrApiClient;
import com.pengrad.telegrambot.TelegramBot;
import dagger.Module;
import dagger.Provides;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import redis.clients.jedis.JedisPool;

import javax.inject.Singleton;
import java.util.Map;
import java.util.Properties;

@Module
class FakeConfig {
    private final Properties properties = ApplicationConfiguration.getApplicationProperties("/test.properties");
    private final Map<String, String> dataSourceConfig = ApplicationConfiguration.getConfigMap(
        "jakarta.persistence.jdbc.url",
        "jakarta.persistence.jdbc.user",
        "jakarta.persistence.jdbc.password"
    );

    private final FakeTelegramBot fakeTelegramBot = new FakeTelegramBot();

    @Provides
    @Singleton
    EntityManager entityManager() {
        return Persistence.createEntityManagerFactory("test", dataSourceConfig).createEntityManager();
    }

    @Provides
    @Singleton
    JedisPool redisPool() {
        var path = properties.getProperty("jedis.redis.path");
        return new JedisPool(path);
    }

    @Provides
    @Singleton
    CbrApiClient mockedCbrApiClient() {
        return new MockedCbrApiClient();
    }

    @Provides
    @Singleton
    FakeTelegramBot fakeTelegramBot() {
        return fakeTelegramBot;
    }

    @Provides
    @Singleton
    TelegramBot telegramBot() {
        return fakeTelegramBot;
    }
}
