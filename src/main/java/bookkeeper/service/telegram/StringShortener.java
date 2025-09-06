package bookkeeper.service.telegram;

/**
 * Class used to compact callback messages for buttons.
 * <p>
 * callback_data have 64 character length limit (see Telegram API docs).
 * Knowing it, your both type string and data must be at total less than 55 characters.
 * API error ( error event) will return otherwise.
 */
class StringShortener {
    private final int maxStringLength;
    private final StringShortenerCache cache;

    StringShortener(int maxLength, StringShortenerCache cache) {
        this.maxStringLength = maxLength;
        this.cache = cache;
    }

    String shrink(String input) {
        if (input.length() <= maxStringLength)
            return input;

        var shrinked = "#%s".formatted(input.hashCode());
        if (shrinked.length() > maxStringLength)
            throw new RuntimeException("Not enough shrinked");

        cache.put(shrinked, input);
        return shrinked;
    }

    String unshrink(String shrinked) {
        var result = cache.get(shrinked);
        return result != null ? result : shrinked;
    }
}
