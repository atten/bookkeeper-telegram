package bookkeeper.telegram.scenarios.store.tinkoff;

import bookkeeper.entities.TelegramUser;
import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.repositories.MerchantExpenditureRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.services.matchers.ExpenditureMatcherByMerchant;
import bookkeeper.services.registries.TransactionParserRegistry;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.pengrad.telegrambot.model.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TransactionParserRegistryFactoryTinkoffTest {
    private static TransactionParserRegistry registry;
    private static TelegramUser user;

    @BeforeAll
    static void prepare() {
        var entityManager = Persistence.createEntityManagerFactory("test").createEntityManager();
        var accountRepository = new AccountRepository(entityManager);
        var merchantExpenditureRepository = new MerchantExpenditureRepository(entityManager);
        var expenditureMatcherByMerchant = new ExpenditureMatcherByMerchant(merchantExpenditureRepository);
        var userRepository = new TelegramUserRepository(entityManager);
        var telegramUser = new User(123L);

        user = userRepository.getOrCreate(telegramUser);
        registry = new TransactionParserRegistryFactoryTinkoff(accountRepository, expenditureMatcherByMerchant).create();
    }

    /**
     * Just check valid messages are parsed into transactions
     */
    @Test
    void parseOk() throws ParseException, URISyntaxException, IOException {
        var path = Path.of(Objects.requireNonNull(this.getClass().getResource("/validMessagesTinkoff.txt")).toURI());
        var stream = Files.lines(path);
        var lines = stream.collect(Collectors.toList());

        for (var line : lines) {
            var transaction = registry.parse(line, user);
            assert(!transaction.isEmpty());
        }

        stream.close();
    }

    /**
     * Just check valid messages are parsed into transactions
     */
    @Test
    void parseEmpty() throws ParseException, URISyntaxException, IOException {
        var path = Path.of(Objects.requireNonNull(this.getClass().getResource("/emptyMessagesTinkoff.txt")).toURI());
        var stream = Files.lines(path);
        var lines = stream.collect(Collectors.toList());

        for (var line : lines) {
            var transaction = registry.parse(line, user);
            assert(transaction.isEmpty());
        }

        stream.close();
    }

    /**
     * Just check invalid message results in ParseError
     */
    @Test
    void parseFail() {
        var rawMessage = "Хер пойми что";
        assertThrows(ParseException.class, () -> registry.parse(rawMessage, user));
    }
}