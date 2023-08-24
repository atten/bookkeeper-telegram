package bookkeeper.telegram;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class Config {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    String botToken() {
        return System.getenv("BOT_TOKEN");
    }

    /**
     There's only one instance of database writer (the bot itself), so we can use a single persistence context throughout runtime.
     */
    EntityManager entityManager() {
        var em = Persistence.createEntityManagerFactory("default", dataSourceConfig()).createEntityManager();
        migrate(em);
        return em;
    }

    private void migrate(EntityManager entityManager) {
        String sql;
        try {
            var path = Path.of(Objects.requireNonNull(this.getClass().getResource("/init_database.sql")).toURI());
            sql = Files.readString(path);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

        if (sql.isEmpty())
            return;

        var query = entityManager.createNativeQuery(sql);
        try {
            query.getSingleResult();
        } catch (HibernateException e) {
            logger.warn(e.toString());
        }
    }

    private Map<String, String> dataSourceConfig() {
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
