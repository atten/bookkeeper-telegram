package bookkeeper.telegram.scenario.addTransaction.sber;

import bookkeeper.service.telegram.AbstractHandler;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@SuppressWarnings("unused")
@Module
public abstract class SberHandlersModule {
    @Binds
    @IntoSet
    abstract AbstractHandler tinkoffSmsHandler(SberSmsHandler handler);
}
