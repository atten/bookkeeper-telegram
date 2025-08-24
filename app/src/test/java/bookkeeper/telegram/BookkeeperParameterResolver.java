package bookkeeper.telegram;

import bookkeeper.dao.entity.Account;
import bookkeeper.dao.entity.AccountTransaction;
import bookkeeper.dao.entity.TelegramUser;
import bookkeeper.resolverAnnotations.Month;
import bookkeeper.resolverAnnotations.PreviousMonth;
import bookkeeper.resolverAnnotations.Raw;
import com.pengrad.telegrambot.model.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.extension.*;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class BookkeeperParameterResolver implements ParameterResolver, BeforeEachCallback {
    private final Random random = new Random();
    private final List<Class<?>> supportedClasses = List.of(User.class, TelegramUser.class, Account.class, AccountTransaction.class, FakeSession.class, EntityManager.class);

    private final ArrayList<User> userCache = new ArrayList<>();
    private final ArrayList<TelegramUser> telegramUserCache = new ArrayList<>();
    private final ArrayList<Account> accountCache = new ArrayList<>();
    private final ArrayList<AccountTransaction> accountTransactionCache = new ArrayList<>();

    private User userFactory() {
        var obj = new User(random.nextLong());
        userCache.add(obj);
        return obj;
    }

    private TelegramUser telegramUserFactory() {
        var obj = new TelegramUser();
        obj.setTelegramId(userCache.stream().findAny().orElseGet(this::userFactory).id());
        obj.setFirstAccess(LocalDate.now());
        obj.setLastAccess(LocalDate.now());
        obj.setLanguageCode("ru");

        FakeApp.container.entityManager().persist(obj);
        telegramUserCache.add(obj);
        return obj;
    }

    private Account accountFactory() {
        var obj = new Account();
        obj.setName("Account %s".formatted(accountCache.size()));
        obj.setCurrency(Currency.getInstance("RUB"));
        obj.setTelegramUser(telegramUserCache.stream().findAny().orElseGet(this::telegramUserFactory));
        obj.setCreatedAt(Instant.now());

        FakeApp.container.entityManager().persist(obj);
        accountCache.add(obj);
        return obj;
    }

    private Account accountFactory(Parameter parameter) {
        var obj = new Account();

        if (parameter.isAnnotationPresent(bookkeeper.resolverAnnotations.Currency.class)) {
            var currency = Currency.getInstance(parameter.getAnnotation(bookkeeper.resolverAnnotations.Currency.class).currency());
            obj.setCurrency(currency);
        } else {
            obj.setCurrency(Currency.getInstance("RUB"));
        }

        if (parameter.isAnnotationPresent(bookkeeper.resolverAnnotations.Name.class)) {
            var name = parameter.getAnnotation(bookkeeper.resolverAnnotations.Name.class).name();
            obj.setName(name);
        } else {
            obj.setName("Account %s".formatted(accountCache.size()));
        }

        if (parameter.isAnnotationPresent(bookkeeper.resolverAnnotations.Hidden.class)) {
            obj.setHidden(true);
        }

        obj.setTelegramUser(telegramUserCache.stream().findAny().orElseGet(this::telegramUserFactory));
        obj.setCreatedAt(Instant.now());

        FakeApp.container.entityManager().persist(obj);
        accountCache.add(obj);
        return obj;
    }

    private AccountTransaction accountTransactionFactory() {
        var obj = new AccountTransaction();

        obj.setAmount(BigDecimal.TEN);
        obj.setRaw("Transaction 10 RUB");
        obj.setExpenditure(bookkeeper.enums.Expenditure.OTHER);
        obj.setAccount(accountCache.stream().findFirst().orElseGet(this::accountFactory));
        obj.setCreatedAt(Instant.now());
        obj.setTimestamp(Instant.now());

        FakeApp.container.entityManager().persist(obj);
        return obj;
    }

    private AccountTransaction accountTransactionFactory(Parameter parameter) {
        var obj = new AccountTransaction();

        obj.setCreatedAt(Instant.now());
        obj.setTimestamp(Instant.now());

        if (parameter.isAnnotationPresent(Raw.class)) {
            var raw = parameter.getAnnotation(Raw.class).raw();
            obj.setRaw(raw);
        } else {
            obj.setRaw("Transaction 10 RUB");
        }

        if (parameter.isAnnotationPresent(PreviousMonth.class)) {
            obj.setTimestamp(LocalDateTime.now().minusMonths(1).toInstant(ZoneOffset.UTC));
        }

        if (parameter.isAnnotationPresent(Month.class)) {
            var now = LocalDate.now();
            var month = parameter.getAnnotation(Month.class).month();
            obj.setTimestamp(LocalDate.of(now.getYear(), month, 1).atTime(12, 0).toInstant(ZoneOffset.UTC));
        }

        if (parameter.isAnnotationPresent(bookkeeper.resolverAnnotations.Amount.class)) {
            var amount = parameter.getAnnotation(bookkeeper.resolverAnnotations.Amount.class).amount();
            obj.setAmount(new BigDecimal("%s".formatted(amount)));
        } else {
            obj.setAmount(BigDecimal.TEN);
        }

        if (parameter.isAnnotationPresent(bookkeeper.resolverAnnotations.Expenditure.class)) {
            var expenditure = parameter.getAnnotation(bookkeeper.resolverAnnotations.Expenditure.class).value();
            obj.setExpenditure(expenditure);
        } else {
            obj.setExpenditure(bookkeeper.enums.Expenditure.OTHER);
        }

        if (parameter.isAnnotationPresent(bookkeeper.resolverAnnotations.Currency.class)) {
            var currency = Currency.getInstance(parameter.getAnnotation(bookkeeper.resolverAnnotations.Currency.class).currency());
            obj.setAccount(accountCache.stream().filter(account -> account.getCurrency().equals(currency)).findAny().orElseGet(() -> accountFactory(parameter)));
        } else {
            obj.setAccount(accountCache.stream().findFirst().orElseGet(this::accountFactory));
        }


        FakeApp.container.entityManager().persist(obj);
        return obj;
    }

    private FakeSession fakeSessionFactory() {
        var user = userCache.stream().findAny().orElseGet(this::userFactory);
        return new FakeSession(user, FakeApp.container.bot(), FakeApp.container.fakeTelegramBot());
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return supportedClasses.contains(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        var parameter = parameterContext.getParameter();
        var type = parameter.getType();
        var hasAnnotations = parameter.getAnnotations().length > 0;

        if (type.equals(Account.class)) {
            if (!hasAnnotations)
                return accountCache.stream().findAny().orElseGet(this::accountFactory);
            return accountFactory(parameter);
        }

        if (type.equals(AccountTransaction.class)) {
            if (!hasAnnotations)
                return accountTransactionCache.stream().findAny().orElseGet(this::accountTransactionFactory);
            return accountTransactionFactory(parameter);
        }

        if (type.equals(FakeSession.class)) {
            return fakeSessionFactory();
        }

        if (type.equals(EntityManager.class)) {
            return FakeApp.container.entityManager();
        }

        throw new ParameterResolutionException(type.toString());
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        clearDatabase(FakeApp.container.entityManager());
        clearCache();
    }

    private void clearCache() {
        userCache.clear();
        telegramUserCache.clear();
        accountCache.clear();
        accountTransactionCache.clear();
    }

    private static void clearDatabase(EntityManager entityManager) {
        String sqlPath = "clear_database.sql";
        String sql;
        try {
            var resourcePath = Path.of(Objects.requireNonNull(Config.class.getResource("/" + sqlPath)).toURI());
            sql = Files.readString(resourcePath);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

        var query = entityManager.createNativeQuery(sql);
        entityManager.getTransaction().begin();
        query.executeUpdate();
        entityManager.getTransaction().commit();
    }
}
