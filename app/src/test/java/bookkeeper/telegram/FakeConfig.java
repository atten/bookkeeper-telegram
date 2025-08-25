package bookkeeper.telegram;

import bookkeeper.service.ApplicationConfiguration;
import bookkeeper.service.client.CbrApiClient;
import bookkeeper.service.client.MockedCbrApiClient;
import bookkeeper.service.telegram.StringShortenerCache;
import bookkeeper.service.telegram.StringShortenerCacheMap;
import com.pengrad.telegrambot.TelegramBot;
import com.sun.net.httpserver.HttpServer;
import dagger.Module;
import dagger.Provides;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;

@Module
class FakeConfig {
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
    StringShortenerCache stringShortenerCache() {
        return new StringShortenerCacheMap();
    }

    @Provides
    @Singleton
    CbrApiClient mockedCbrApiClient() {
        return new MockedCbrApiClient();
    }

    @Provides
    @Singleton
    Optional<HttpServer> webhookServer() { return Optional.empty(); }

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
