package bookkeeper.telegram.scenario.addTransaction;

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
            "еда 220 руб",
            "еда 220 rub",
            "еда 220 RUB",
            "еда 220 $",
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
            "Покупка, счет RUB. 350 RUB. FERMERPQR. Доступно 225,62 RUB",
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
            "MIR-1234 16:00 Покупка 198р 36,6_6806 Баланс: 1 681.81р",
            "MIR-1234 19:25 Покупка 6.50BYN (198.90р) YANDEX GO Баланс: 1 246.81р",
            "MIR-1234 20:55 Покупка по СБП 733.52р Прием платежей mos Баланс: 2 634.48р",
            "СЧЁТ1234 01:27 Оплата 70р за уведомления по СберКартам. Следующее списание 23.08.24. Баланс 2 611,81р",
            "СЧЁТ1234 09:58 Оплата 550р Автоплатёж Энторнет Баланс: 1 378.52р",
            "СЧЁТ1234 16:40 Оплата 223р Комиссия 22.23р АО \"П-Ц\" Баланс: 360.73р",
            "СЧЁТ1234 16:36 Зачисление 1 000р Баланс: 1 123.48р",
            "СЧЁТ1234 00:21 Перевод 140р от Сергей С. Баланс: 879.81р",
            "MIR-1234 04:27 Перевод из Т‑Банк +1000р от ОЛЕГ В. Баланс: 1888.45р",
            "MIR-1234 12:57 перевод 593р Т-Банк Баланс: 1 891.76р",
            "СЧЁТ1234 18:26 перевод 1 000р Баланс: 3 879.81р",
            "СЧЁТ1234 19:03 Оплата 550р OPLATA USLUG MOS Баланс: 1 394.21р",
            "СЧЁТ1234 19:03 Оплата 550р Баланс: 1 394.21р",
            "MIR-1234 22:13 Отмена покупки 11р ЯндексGo Баланс: 1 217.51р",
            "MIR-1234 06.10.24 12:32 возврат покупки 1р Boosty Баланс: 1 540.42р",
            "MIR-1234 11:36 Выплата кешбэка по СБП 65.54р CSHBCK_АО НСПК_В2С Баланс: 2 138.44р",
            "Накопит. счет Премьер *1234 Капитализация на 714,75р. Баланс: 1 714,75р. Подробнее s.sber.ru/ABCDE"
        );
    }

    private static Collection<String> emptySberInputs() {
        return List.of(
            "Автоперевод «ABC» со счёта *1234 клиенту Иван Пупкин Б. на 100р изменён. Следующий перевод 01.09.24.",
            "Автоперевод «ABC» клиенту Иван Пупкин Б. не исполнен по техническим причинам.",
            "Вход в СберБанк Онлайн в 19:01 по московскому времени. Если входили не вы, позвоните на 900.",
            "Никому не сообщайте код 123456 для входа в СберБанк Онлайн.\n@online.sberbank.ru #789012", // contains newline!
            "Никому не сообщайте код 1234 Списание 11р с MIR-1234 ЯндексGo",
            "Счёт на оплату от Some Merchant 1-2-3 уже в приложении СберБанк Онлайн https://s.sber.ru/abcdef",
            "Подключение автоплатежа «ABC», карта списания 1234, сумма 550p, ближайший платёж 01.09.24. Код - 123456. Никому его не сообщайте.",
            "Автоплатёж «ABC» подключён. Первый платёж со счёта *1234 на 550р будет 01.09.24. Накануне вы получите уведомление с возможностью отмены платежа. Подробнее s.sber.ru/abcdef",
            "Автоплатёж «ABC» изменён",
            "МИР1234 15:01 Пополнение 34,21р через СБП из Банк Русский Стандарт от АО \"НСПК\". Сообщение: Выплата кешбэка по оплате через СБП",
            "Участник программы «СберСпасибо» перевёл вам 52,75 бонуса. Баланс: 234,22 бонуса.",
            "За прошлую неделю вам начислили 32 бонуса Спасибо. Проверить баланс можно по ссылке s.sber.ru/abcdef",
            "На прошлой неделе вам начислили 350 бонусов Спасибо. Посмотреть баланс и категории кешбэка s.sber.ru/abcdef",
            "В прошлом месяце повысилась сумма на ваших счетах или увеличились траты со Сбером, поэтому в этом месяце доступно больше привилегий Нового СберПремьера. Подробнее s.sber.ru/abcde"
        );
    }

    private static Collection<String> emptyTinkoffInputs() {
        return List.of(
            "Покупка, карта *0964. 1 RUB. Mos.Transport. Доступно 649.99 RUB",
            "Никому не говорите код 1234! Вход в Тинькофф в 17:30 19.08.23",
            "Вход в личный кабинет Т‑Банка",
            "Завтра выполним регулярный платеж \"Квартира\" на 15 000 р",
            "Завтра выполним автоплатеж «мегафона» на 300 р.",
            "Посмотрите ответ по вашему обращению в чате: t.tb.ru/abcdef",
            "Оцените решение вопроса q.tinkoff.ru/abcdef",
            "Детали полета и маршрутная квитанция по заказу 0O0O0O q.tinkoff.ru/abcdef",
            "По вашему рейсу произошли изменения. Отправили информацию на email.",
            "Изменилось расписание рейса по заказу XXXXXX q.tb.ru/ABCDEF",
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
    void addSingleTransactionAndApprove(FakeSession session) {
        session
            .sendText("фантики 15")
            .expectContains("Добавлена запись")
            .pressButton("Подтвердить")
            .expectContains("Добавлена запись");
    }

    @Test
    void addMultipleFreehandTransactions(FakeSession session) {
        session
            .sendText("еда 220\nтранспорт 1000")
            .expectContains("Добавлены 2 записи")
            .expectContains("на сумму 1220 RUB");
    }

    @Test
    void addFreehandTransactionsAndDetectCurrency(FakeSession session) {
        session
            .sendText("картошка 220 BYN")
            .sendText("капуста 100 BYN")
            .sendText("крокодил 1000 USD")
            .sendText("лук 100")  // BYN is expected to be most used currency despite USD was the latest
            .expectContains("на сумму 100 BYN");
    }

    @Test
    void showWarningMessageOnPartialSuccess(FakeSession session) {
        session
            .sendText("еда 220\nblahblah")
            .expectHistoryContains("Добавлена запись")
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
    @MethodSource({"emptyFreehandInputs", "emptyTinkoffInputs", "emptySberInputs"})
    void skipEmptyTransaction(String input, FakeSession session) {
        session.sendText(input).expect("Не добавлено ни одной записи");
    }
}
