package bookkeeper.telegram.scenarios.addTransactions;

import bookkeeper.entities.AccountTransaction;
import bookkeeper.entities.TelegramUser;
import bookkeeper.services.registries.TransactionParserRegistry;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Template class to be extended by tests of particular TransactionParserRegistry
 */
public class TransactionParserRegistryCommonTest {
    protected static TransactionParserRegistry registry;
    protected static TelegramUser user;
    protected static String validMessagesPath;
    protected static String emptyMessagesPath;

    /**
     * Just check valid messages are parsed into transactions
     */
    @Test
    void parseOk() throws ParseException, URISyntaxException, IOException {
        if (user == null || registry == null || validMessagesPath == null)
            return;

        var path = Path.of(Objects.requireNonNull(this.getClass().getResource(validMessagesPath)).toURI());
        var lines = Files.readAllLines(path).toArray(new String[0]);
        var transactions = registry.parseMultiple(lines, user);
        assert transactions.size() == lines.length;
        assert transactions.stream().noneMatch(AccountTransaction::isEmpty);
    }

    /**
     * Just check valid messages are parsed but transactions list is empty
     */
    @Test
    void parseEmpty() throws ParseException, URISyntaxException, IOException {
        if (user == null || registry == null || emptyMessagesPath == null)
            return;

        var path = Path.of(Objects.requireNonNull(this.getClass().getResource(emptyMessagesPath)).toURI());
        var lines = Files.readAllLines(path).toArray(new String[0]);
        var transactions = registry.parseMultiple(lines, user);
        assert transactions.isEmpty();
    }

    /**
     * Just check invalid message results in ParseError
     */
    @Test
    void parseFail() throws URISyntaxException, IOException {
        if (user == null || registry == null)
            return;

        var errorMessagesPath = "/errorMessagesAll.txt";
        var path = Path.of(Objects.requireNonNull(this.getClass().getResource(errorMessagesPath)).toURI());
        var lines = Files.readAllLines(path);

        for (var line : lines) {
            assertThrows(ParseException.class, () -> registry.parse(line, user));
        }
    }
}