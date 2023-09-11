package bookkeeper.telegram.scenario.addTransaction.tinkoff;

import bookkeeper.service.repository.AccountRepository;
import bookkeeper.service.repository.MerchantExpenditureRepository;
import bookkeeper.service.repository.TelegramUserRepository;
import bookkeeper.service.matcher.ExpenditureMatcherByMerchant;
import bookkeeper.telegram.scenario.addTransaction.TransactionParserRegistryCommonTest;
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