package bookkeeper.telegram.scenario;

import bookkeeper.telegram.BookkeeperParameterResolver;
import bookkeeper.telegram.FakeSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.List;

@ExtendWith(BookkeeperParameterResolver.class)
class AddTransactionScenarioTest {
    private static Collection<String> validFreehandInputs() {
        return List.of(
            "еда 220",
            "еда 220 USD",
            "еда 220 usd",
            "транспорт 1000",
            "транспорт 100.50",
            "транспорт 100,50",
            "на метро 150",
            "на метро 150 EUR"
        );
    }

    private static Collection<String> emptyFreehandInputs() {
        return List.of(
            "покупка 0",
            "покупка 0 RUB"
        );
    }

    private static Collection<String> validTinkoffInputs() {
        return List.of(
            "Покупка, карта *0964. 621.8 RUB. VKUSVILL 2. Доступно 499.28 RUB",
            "Покупка 17.07.2023. Карта *0964. 56 RUB. MOS.TRANSP. Доступно 499.28 RUB",
            "Перевод. Счет RUB. 500 RUB. Сергей С. Баланс 653.04 RUB",
            "Платеж. Счет RUB. 360 RUB. Йота. Баланс 416.84 RUB",
            "Выполнен регулярный платеж \"на мегафон\" на 360 р.",
            "Оплата СБП, счет RUB. 1760 RUB. YANDEX.AFISHA. Доступно 9480.79 RUB",
            "Пополнение, счет RUB. 800 RUB.  Доступно 713.79 RUB", // contains double space!
            "Пополнение, счет RUB. 800 RUB. Сергей С. Доступно 713.79 RUB",
            "Пополнение. Счет RUB. 800 RUB. Сергей С. Доступно 713.79 RUB",
            "Возврат. Счет RUB. 2070 RUB. Доступно 3000 RUB",
            "Возврат СБП, счет RUB. 104.7 RUB.  Доступно 1620.61 RUB",
            "Выплата процентов по вкладу: 1 872.95 RUB",
            "Снятие, карта *0964. 3000 RUB. ATM 123. Доступно 343.32 RUB"
        );
    }

    private static Collection<String> emptyTinkoffInputs() {
        return List.of(
            "Покупка, карта *0964. 1 RUB. Mos.Transport. Доступно 649.99 RUB",
            "Никому не говорите код 1234! Вход в Тинькофф в 17:30 19.08.23",
            "Завтра выполним регулярный платеж \"Квартира\" на 15 000 р",
            "Оцените решение вопроса q.tinkoff.ru/abcdef",
            "Детали полета и маршрутная квитанция по заказу 0O0O0O q.tinkoff.ru/abcdef",
            "Полис путешественника оплачен и доступен в приложении Тинькофф. Если будут вопросы, пишите в чат или звоните: 123456789",
            "Отказ ABC. Неправильный ПИН-код. Карта *1234"
        );
    }

    @ParameterizedTest
    @MethodSource({"validFreehandInputs", "validTinkoffInputs"})
    void addSingleTransaction(String input, FakeSession session) {
        session.sendText(input).expectStartsWith("Добавлена запись на счёт");
    }

    @Test
    void addMultipleFreehandTransactions(FakeSession session) {
        session
            .sendText("еда 220\nтранспорт 1000")
            .expectContains("Добавлены 2 записи")
            .expectContains("на сумму 1220 RUB");
    }

    @Test
    void showErrorMessageOnPartialSuccess(FakeSession session) {
        session
            .sendText("еда 220\nblahblah")
            .expectContains("Ошибка");
    }

    @ParameterizedTest
    @MethodSource({"emptyFreehandInputs", "emptyTinkoffInputs"})
    void skipEmptyTransaction(String input, FakeSession session) {
        session.sendText(input).expect("Не добавлено ни одной записи");
    }
}
