package bookkeeper.service.telegram;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

public class StringShortenerCacheRedis implements StringShortenerCache{
    private final SetParams SET_PARAMS = new SetParams().ex(3600 * 24 * 365);
    private final JedisPool jedisPool;

    public StringShortenerCacheRedis(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public void put(String key, String value) {
        try (var redis = jedisPool.getResource()) {
            redis.set(key, value, SET_PARAMS);
        }
    }

    @Override
    public String get(String key) {
        try (var redis = jedisPool.getResource()) {
            return redis.get(key);
        }
    }
}
