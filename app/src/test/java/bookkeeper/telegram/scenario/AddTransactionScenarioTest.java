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
            " на метро 150 ", // starts and ends with space
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
            "Выполнен автоплатеж «на мегафон» на 360 р.",
            "Оплата СБП, счет RUB. 1760 RUB. YANDEX.AFISHA. Доступно 9480.79 RUB",
            "Пополнение, счет RUB. 800 RUB.  Доступно 713.79 RUB", // contains double space!
            "Пополнение. Счет RUB. 800 RUB. Сергей С. Доступно 713.79 RUB",
            "Пополнение, счет RUB. 800 RUB. Сергей С. Доступно 713,79 RUB", // commas instead of space
            "Возврат. Счет RUB. 2070 RUB. Доступно 3000 RUB",
            "Возврат СБП, счет RUB. 104.7 RUB.  Доступно 1620.61 RUB",
            "Выплата процентов по вкладу: 1 872.95 RUB",
            "Снятие, карта *0964. 3000 RUB. ATM 123. Доступно 343.32 RUB"
        );
    }

    private static Collection<String> validSberInputs() {
        return List.of(
            "MIR-1234 16:00 Покупка 198р PEKARNYA Баланс: 1 681.81р",
            "MIR-1234 20:55 Покупка по СБП 733.52р Прием платежей mos Баланс: 2 634.48р",
            "СЧЁТ1234 01:27 Оплата 70р за уведомления по СберКартам. Следующее списание 23.08.24. Баланс 2 611,81р",
            "СЧЁТ1234 16:36 Зачисление 1 000р Баланс: 1 123.48р",
            "СЧЁТ1234 00:21 Перевод 140р от Сергей С. Баланс: 879.81р",
            "СЧЁТ1234 18:26 перевод 1 000р Баланс: 3 879.81р"
        );
    }

    private static Collection<String> emptyTinkoffInputs() {
        return List.of(
            "Покупка, карта *0964. 1 RUB. Mos.Transport. Доступно 649.99 RUB",
            "Никому не говорите код 1234! Вход в Тинькофф в 17:30 19.08.23",
            "Завтра выполним регулярный платеж \"Квартира\" на 15 000 р",
            "Завтра выполним автоплатеж «мегафона» на 300 р.",
            "Посмотрите ответ по вашему обращению в чате: t.tb.ru/abcdef",
            "Оцените решение вопроса q.tinkoff.ru/abcdef",
            "Детали полета и маршрутная квитанция по заказу 0O0O0O q.tinkoff.ru/abcdef",
            "По вашему рейсу произошли изменения. Отправили информацию на email.",
            "Полис путешественника оплачен и доступен в приложении Тинькофф. Если будут вопросы, пишите в чат или звоните: 123456789",
            "Посоветуйте карту Black и получите 1 500 р. q.tb.ru/abcdef",
            "Советуйте Premium — подарим 2000 р. и редкую карту. q.tb.ru/abcdef",
            "Дарим акции на сумму до 10 рублей за рекомендацию. q.tb.ru/abcdef",
            "Отказ ABC. Неправильный ПИН-код. Карта *1234",
            "Вклад 1234 закрыт"
        );
    }

    @ParameterizedTest
    @MethodSource({"validFreehandInputs", "validTinkoffInputs", "validSberInputs"})
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
            .expectContains("Ошибка")
            .expectContains("1 / 2 строк не распознано");
    }

    @Test
    void showWarningMessageOnDuplicates(FakeSession session) {
        session
            .sendText("еда 220")
            .sendText("еда 220")
            .expectContains("1 дубль");
    }

    @Test
    void showSummaryOnOverviewClick(FakeSession session) {
        session
            .sendText("еда 220\nтранспорт 1000")
            .pressButton("Разобрать")
            .pressButton("Готово")
            .expectContains("Добавлены 2 записи");
    }

    @ParameterizedTest
    @MethodSource({"emptyFreehandInputs", "emptyTinkoffInputs"})
    void skipEmptyTransaction(String input, FakeSession session) {
        session.sendText(input).expect("Не добавлено ни одной записи");
    }
}
