package bookkeeper.telegram.scenarios.save.tinkoff;

import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.repositories.MerchantExpenditureRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.services.matchers.ExpenditureMatcherByMerchant;
import bookkeeper.telegram.scenarios.save.TransactionParserRegistryCommonTest;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeAll;
import com.pengrad.telegrambot.model.User;


class TransactionParserRegistryFactoryTinkoffTest extends TransactionParserRegistryCommonTest {

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
        validMessagesPath = "/validMessagesTinkoff.txt";
        emptyMessagesPath = "/emptyMessagesTinkoff.txt";
    }
}