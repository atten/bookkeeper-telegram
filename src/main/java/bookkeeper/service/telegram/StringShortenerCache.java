package bookkeeper.service.telegram;

public interface StringShortenerCache {

    void put(String key, String value);

    String get(String key);
}
