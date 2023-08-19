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

import java.text.ParseException;
import java.util.List;

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
    void parseOk() throws ParseException {
        List<String> rawMessages = List.of(
            "Покупка, карта *0964. 621.8 RUB. VKUSVILL 2. Доступно 499.28 RUB",
            "Покупка 17.07.2023. Карта *0964. 56 RUB. MOS.TRANSP. Доступно 499.28 RUB",
            "Перевод. Счет RUB. 500 RUB. Сергей С. Баланс 653.04 RUB",
            "Выполнен регулярный платеж \"на мегафон\" на 360 р."
        );

        for (String rawMessage : rawMessages) {
            var transaction = registry.parse(rawMessage, user);
            assertNotNull(transaction);
        }
    }

    /**
     * Just check valid messages are parsed into transactions
     */
    @Test
    void parseEmpty() throws ParseException {
        List<String> rawMessages = List.of(
            "Покупка, карта *0964. 1 RUB. Mos.Transport. Доступно 649.99 RUB"
        );

        for (String rawMessage : rawMessages) {
            var transaction = registry.parse(rawMessage, user);
            assert(transaction.isEmpty());
        }
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