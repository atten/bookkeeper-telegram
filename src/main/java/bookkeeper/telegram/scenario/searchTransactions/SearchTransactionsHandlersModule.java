package bookkeeper.telegram.scenario.searchTransactions;

import bookkeeper.service.telegram.AbstractHandler;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@SuppressWarnings("unused")
@Module
public abstract class SearchTransactionsHandlersModule {
    @Binds
    @IntoSet
    abstract AbstractHandler searchTransactionsByRawMessageHandler(SearchTransactionsByRawMessageHandler handler);
}
