package bookkeeper.service.telegram;

import bookkeeper.dao.repository.TelegramCallbackMessageRepository;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class StringShortenerCacheDb implements StringShortenerCache{
    private final TelegramCallbackMessageRepository repository;
    private final Duration maxAge = Duration.of(30, ChronoUnit.DAYS);
    private int putRequestsCount = 0;

    public StringShortenerCacheDb(TelegramCallbackMessageRepository repository) {
        this.repository = repository;
    }

    @Override
    public void put(String key, String value) {
        repository.saveMessage(key, value);

        // perform cleanup upon every 1000 save requests (1th, 1001th...)
        if (putRequestsCount++ % 1000 == 0) {
            repository.deleteOldMessages(maxAge);
        }
    }

    @Override
    public String get(String key) {
        return repository.findMessage(key).orElse(null);
    }
}
