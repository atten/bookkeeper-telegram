package bookkeeper.telegram.scenarios.store.freehand;

import bookkeeper.entities.TelegramUser;
import bookkeeper.services.matchers.ExpenditureMatcherByMerchant;
import bookkeeper.services.registries.TransactionParserRegistry;
import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.repositories.AccountTransactionRepository;
import bookkeeper.services.repositories.MerchantExpenditureRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import com.pengrad.telegrambot.model.User;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class TransactionParserRegistryFactoryFreehandTest {
    private static TransactionParserRegistry registry;
    private static TelegramUser user;

    @BeforeAll
    static void prepare() {
        var entityManager = Persistence.createEntityManagerFactory("test").createEntityManager();
        var accountRepository = new AccountRepository(entityManager);
        var transactionRepository = new AccountTransactionRepository(entityManager);
        var merchantExpenditureRepository = new MerchantExpenditureRepository(entityManager);
        var expenditureMatcherByMerchant = new ExpenditureMatcherByMerchant(merchantExpenditureRepository);
        var userRepository = new TelegramUserRepository(entityManager);
        var telegramUser = new User(123L);

        user = userRepository.getOrCreate(telegramUser);
        registry = new TransactionParserRegistryFactoryFreehand(accountRepository, transactionRepository, expenditureMatcherByMerchant).create();
    }

    /**
     * Just check valid messages are parsed but transactions list is empty
     */
    @Test
    void parseEmpty() throws ParseException, URISyntaxException, IOException {
        var path = Path.of(Objects.requireNonNull(this.getClass().getResource("/emptyMessagesFreehand.txt")).toURI());
        var lines = Files.readAllLines(path).toArray(new String[0]);
        var transactions = registry.parseMultiple(lines, user);
        assert transactions.isEmpty();
    }

    /**
     * Just check invalid message results in ParseError
     */
    @Test
    void parseFail() throws URISyntaxException, IOException {
        var path = Path.of(Objects.requireNonNull(this.getClass().getResource("/errorMessagesAll.txt")).toURI());
        var lines = Files.readAllLines(path);

        for (var line : lines) {
            assertThrows(ParseException.class, () -> registry.parse(line, user));
        }
    }
}