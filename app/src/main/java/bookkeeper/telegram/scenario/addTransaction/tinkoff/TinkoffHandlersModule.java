package bookkeeper.telegram.scenario.addTransaction.tinkoff;

import bookkeeper.telegram.shared.AbstractHandler;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@SuppressWarnings("unused")
@Module
public abstract class TinkoffHandlersModule {
    @Binds
    @IntoSet
    abstract AbstractHandler tinkoffSmsHandler(TinkoffSmsHandler handler);
}
