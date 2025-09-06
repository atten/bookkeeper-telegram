package bookkeeper.service.telegram;

import java.util.LinkedHashMap;
import java.util.Map;

public class StringShortenerCacheMap implements StringShortenerCache{
    private final Map<String, String> cache = new LinkedHashMap<>();

    @Override
    public void put(String key, String value) {
        cache.put(key, value);
    }

    @Override
    public String get(String key) {
        return cache.get(key);
    }
}
