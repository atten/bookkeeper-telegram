package bookkeeper.telegram.shared;

import org.junit.platform.commons.util.LruCache;

import java.util.Map;

/**
 * Class used to compact callback messages for buttons.
 * <p>
 * callback_data have 64 character length limit (see Telegram API docs).
 * Knowing it, your both type string and data must be at total less than 55 characters.
 * API error ( error event) will return otherwise.
 */
class StringShortener {
    private final int CACHE_SIZE = 1000;

    private final Map<String, String> cache = new LruCache<>(CACHE_SIZE);
    private final int maxStringLength;

    static final StringShortener FOR_TELEGRAM_CALLBACK = new StringShortener(55);

    private StringShortener(int maxLength) {
        this.maxStringLength = maxLength;
    }

    String shrink(String input) {
        if (input.length() <= maxStringLength)
            return input;

        var shrinked = String.format("#%s", input.hashCode());
        if (shrinked.length() > maxStringLength)
            throw new RuntimeException("Not enough shrinked");

        cache.putIfAbsent(shrinked, input);
        return shrinked;
    }

    String unshrink(String shrinked) {
        return cache.getOrDefault(shrinked, shrinked);
    }
}
