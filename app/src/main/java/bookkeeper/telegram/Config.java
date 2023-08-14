package bookkeeper.telegram;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Config {
    public String botToken() {
        return System.getenv("BOT_TOKEN");
    }

    public Map<String, String> dataSourceConfig() {
        Map<String, String> result = new HashMap<>();

        List.of(
            "jakarta.persistence.jdbc.url",
            "jakarta.persistence.jdbc.user",
            "jakarta.persistence.jdbc.password"
        ).forEach(s -> {
            var value = System.getenv(s);
            if (!Objects.equals(value, ""))
                result.put(s, value);
        } );

        return result;
    }
}
