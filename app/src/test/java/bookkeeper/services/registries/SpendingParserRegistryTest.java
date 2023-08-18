package bookkeeper.services.registries;

import bookkeeper.services.parsers.Spending;
import bookkeeper.telegram.scenarios.store.tinkoff.parsers.TinkoffPurchaseSmsParser;
import bookkeeper.telegram.scenarios.store.tinkoff.parsers.TinkoffPurchaseSmsWithDateParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpendingParserRegistryTest {
    private final String TINKOFF_SMS_1 = "Покупка, карта *0964. 621.8 RUB. VKUSVILL 2. Доступно 499.28 RUB";

    @Test
    void parseMultiple_success() {
        SpendingParserRegistry parserRegistry = new SpendingParserRegistry()
                .add(new TinkoffPurchaseSmsParser())
                .add(new TinkoffPurchaseSmsWithDateParser());

        String TINKOFF_SMS_2 = "Покупка 17.07.2023. Карта *0964. 56 RUB. MOS.TRANSP. Доступно 499.28 RUB";
        List<Spending> result = parserRegistry.parseMultiple(TINKOFF_SMS_1, TINKOFF_SMS_2);

        assertEquals(2, result.size());
    }

    @Test
    void parseMultiple_partialSuccess() {
        SpendingParserRegistry parserRegistry = new SpendingParserRegistry()
                .add(new TinkoffPurchaseSmsParser())
                .add(new TinkoffPurchaseSmsWithDateParser());

        String UNDEFINED_SMS = "Хер пойми что";
        List<Spending> result = parserRegistry.parseMultiple(TINKOFF_SMS_1, UNDEFINED_SMS);

        assertEquals(1, result.size());
    }
}
