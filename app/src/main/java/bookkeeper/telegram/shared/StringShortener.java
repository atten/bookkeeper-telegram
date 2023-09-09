package bookkeeper.telegram.shared;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

/**
 * Class used to compact callback messages for buttons.
 * <p>
 * callback_data have 64 character length limit (see Telegram API docs).
 * Knowing it, your both type string and data must be at total less than 55 characters.
 * API error ( error event) will return otherwise.
 */
class StringShortener {
    private final SetParams SET_PARAMS = new SetParams().ex(3600 * 24 * 365);

    private final JedisPool jedisPool;
    private final int maxStringLength;

    StringShortener(int maxLength, JedisPool jedisPool) {
        this.maxStringLength = maxLength;
        this.jedisPool = jedisPool;
    }

    String shrink(String input) {
        if (input.length() <= maxStringLength)
            return input;

        var shrinked = String.format("#%s", input.hashCode());
        if (shrinked.length() > maxStringLength)
            throw new RuntimeException("Not enough shrinked");

        try (var redis = jedisPool.getResource()) {
            redis.set(shrinked, input, SET_PARAMS);
        }
        return shrinked;
    }

    String unshrink(String shrinked) {
        try (var redis = jedisPool.getResource()) {
            return redis.get(shrinked);
        }
    }
}
