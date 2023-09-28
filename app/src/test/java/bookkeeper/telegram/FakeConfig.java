package bookkeeper.telegram;

import com.pengrad.telegrambot.TelegramBot;
import dagger.Module;
import dagger.Provides;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import redis.clients.jedis.JedisPool;

import javax.inject.Singleton;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

@Module
class FakeConfig {
    private final FakeTelegramBot fakeTelegramBot = new FakeTelegramBot();

    @Provides
    @Singleton
    EntityManager entityManager() {
        return Persistence.createEntityManagerFactory("test").createEntityManager();
    }

    @Provides
    @Singleton
    JedisPool redisPool() {
        var path = testApplicationProperties().getProperty("jedis.redis.path");
        return new JedisPool(path);
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

    private Properties testApplicationProperties() {
        var p = new Properties();
        var resource = Config.class.getResource("/test.properties");
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
