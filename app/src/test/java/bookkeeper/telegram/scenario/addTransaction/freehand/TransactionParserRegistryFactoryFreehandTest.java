package bookkeeper.telegram.scenario.addTransaction.freehand;

import bookkeeper.service.matcher.ExpenditureMatcherByMerchant;
import bookkeeper.service.repository.AccountRepository;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.service.repository.MerchantExpenditureRepository;
import bookkeeper.service.repository.TelegramUserRepository;
import bookkeeper.telegram.scenario.addTransaction.TransactionParserRegistryCommonTest;
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