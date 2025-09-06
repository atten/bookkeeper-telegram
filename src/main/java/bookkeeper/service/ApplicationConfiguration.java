package bookkeeper.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

public class ApplicationConfiguration {

    public static Properties getApplicationProperties(String resourcePath) {
        var p = new Properties();
        var resource = ApplicationConfiguration.class.getResource(resourcePath);
        Objects.requireNonNull(resource);
        try {
            p.load(new FileInputStream(resource.getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // update values from env variables
        // consider both 'a.b.c' and "A_B_C" keys
        for (var key : p.keySet()) {
            var strKey = (String) key;
            var alternateKey = strKey.replace(".", "_").toUpperCase();
            Stream
                .of(strKey, alternateKey)
                .map(System::getenv)
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresent(value -> p.setProperty(strKey, value));
        }

        return p;
    }

    public static Map<String, String> getConfigMap(String... keys) {
        Map<String, String> result = new HashMap<>();

        // get values from env variables
        // consider both 'a.b.c' and "A_B_C" keys
        for (var key : keys) {
            var alternateKey = key.replace(".", "_").toUpperCase();
            Stream
                .of(key, alternateKey)
                .map(System::getenv)
                .filter(Objects::nonNull)
                .filter(s -> !s.isEmpty())
                .findFirst()
                .ifPresent(value -> result.put(key, value));
        }
        return result;
    }

}
