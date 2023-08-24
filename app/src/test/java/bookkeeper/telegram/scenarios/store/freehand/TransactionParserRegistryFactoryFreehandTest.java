package bookkeeper.telegram.scenarios.store.freehand;

import bookkeeper.services.matchers.ExpenditureMatcherByMerchant;
import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.repositories.AccountTransactionRepository;
import bookkeeper.services.repositories.MerchantExpenditureRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.telegram.scenarios.store.TransactionParserRegistryCommonTest;
import com.pengrad.telegrambot.model.User;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeAll;

class TransactionParserRegistryFactoryFreehandTest extends TransactionParserRegistryCommonTest {

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
        validMessagesPath = "/validMessagesFreehand.txt";
        emptyMessagesPath = "/emptyMessagesFreehand.txt";
    }
}