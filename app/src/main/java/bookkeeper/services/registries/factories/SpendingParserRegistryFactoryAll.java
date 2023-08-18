package bookkeeper.services.registries.factories;

import bookkeeper.telegram.scenarios.store.tinkoff.parsers.TinkoffPurchaseSmsParser;
import bookkeeper.telegram.scenarios.store.tinkoff.parsers.TinkoffPurchaseSmsWithDateParser;
import bookkeeper.telegram.scenarios.store.tinkoff.parsers.TinkoffRecurringChargeSmsParser;
import bookkeeper.telegram.scenarios.store.tinkoff.parsers.TinkoffTransferSmsParser;
import bookkeeper.services.registries.SpendingParserRegistry;

public class SpendingParserRegistryFactoryAll {
    public static SpendingParserRegistry create() {
        return new SpendingParserRegistry()
                .add(new TinkoffTransferSmsParser())
                .add(new TinkoffRecurringChargeSmsParser())
                .add(new TinkoffPurchaseSmsParser())
                .add(new TinkoffPurchaseSmsWithDateParser());
    }
}
