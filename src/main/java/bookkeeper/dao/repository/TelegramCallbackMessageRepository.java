package bookkeeper.dao.repository;

import bookkeeper.dao.entity.TelegramCallbackMessage;
import dagger.Reusable;
import jakarta.persistence.EntityManager;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Reusable
public class TelegramCallbackMessageRepository {
    private final EntityManager manager;

    @Inject
    public TelegramCallbackMessageRepository(EntityManager manager) {
        this.manager = manager;
    }

    public Optional<String> findMessage(String key) {
        var sql = "SELECT message FROM telegram_callback_messages WHERE key = :key";
        var query = manager.createNativeQuery(sql)
            .setParameter("key", key);

        return Optional.ofNullable((String)query.getSingleResultOrNull());
    }

    public void saveMessage(String key, String message) {
        var obj = new TelegramCallbackMessage();
        obj.setKey(key);
        obj.setMessage(message);
        obj.setCreatedAt(Instant.now());
        manager.merge(obj);
    }

    public void deleteOldMessages(Duration maxAge) {
        var sql = "DELETE FROM telegram_callback_messages WHERE created_at < :max_created_at";
        var query = manager.createNativeQuery(sql)
            .setParameter("max_created_at", Instant.now().minusSeconds(maxAge.toSeconds()));
        query.executeUpdate();
    }
}
