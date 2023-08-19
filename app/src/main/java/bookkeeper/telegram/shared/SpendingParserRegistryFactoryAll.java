package bookkeeper.telegram.shared;

import bookkeeper.telegram.scenarios.store.tinkoff.parsers.*;
import bookkeeper.services.registries.SpendingParserRegistry;

public class SpendingParserRegistryFactoryAll {
    public static SpendingParserRegistry create() {
        return new SpendingParserRegistry()
                .add(new TinkoffTransferSmsParser())
                .add(new TinkoffRecurringChargeSmsParser())
                .add(new TinkoffFpsPurchaseSmsParser())
                .add(new TinkoffPurchaseSmsParser())
                .add(new TinkoffPurchaseSmsWithDateParser());
    }
}
