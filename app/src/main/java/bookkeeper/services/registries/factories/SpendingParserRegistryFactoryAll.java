package bookkeeper.services.registries.factories;

import bookkeeper.services.parsers.tinkoff.TinkoffPurchaseSmsParser;
import bookkeeper.services.parsers.tinkoff.TinkoffPurchaseSmsWithDateParser;
import bookkeeper.services.registries.SpendingParserRegistry;

public class SpendingParserRegistryFactoryAll {
    public static SpendingParserRegistry create() {
        return new SpendingParserRegistry()
                .add(new TinkoffPurchaseSmsParser())
                .add(new TinkoffPurchaseSmsWithDateParser());
    }
}
