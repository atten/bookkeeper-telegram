package bookkeeper.services.registries.factories;

import bookkeeper.services.parsers.tinkoff.TinkoffPurchaseSmsParser;
import bookkeeper.services.parsers.tinkoff.TinkoffPurchaseSmsWithDateParser;
import bookkeeper.services.parsers.tinkoff.TinkoffTransferSmsParser;
import bookkeeper.services.registries.SpendingParserRegistry;

public class SpendingParserRegistryFactoryAll {
    public static SpendingParserRegistry create() {
        return new SpendingParserRegistry()
                .add(new TinkoffTransferSmsParser())
                .add(new TinkoffPurchaseSmsParser())
                .add(new TinkoffPurchaseSmsWithDateParser());
    }
}
