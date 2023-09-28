package bookkeeper.telegram.scenario;

import bookkeeper.telegram.FakeApp;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

class AddTransactionScenarioTest {
    @Test
    void addSingleTransaction() throws URISyntaxException, IOException {
        var path = Path.of(Objects.requireNonNull(this.getClass().getResource("/validTransactions.txt")).toURI());
        var lines = Files.readAllLines(path).toArray(new String[0]);

        var session = FakeApp.session();
        for (var line : lines) {
            if (line.isEmpty()) {
                continue;
            }

            session.sendText(line).expectStartsWith("Добавлена запись на счёт");
        }
    }

    @Test
    void skipEmptyTransaction() throws URISyntaxException, IOException {
        var path = Path.of(Objects.requireNonNull(this.getClass().getResource("/emptyTransactions.txt")).toURI());
        var lines = Files.readAllLines(path).toArray(new String[0]);

        var session = FakeApp.session();
        for (var line : lines) {
            if (line.isEmpty()) {
                continue;
            }

            session.sendText(line).expect("Не добавлено ни одной записи");
        }
    }
}
