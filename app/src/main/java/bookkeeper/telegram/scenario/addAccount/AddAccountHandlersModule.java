package bookkeeper.telegram.scenario.addAccount;

import bookkeeper.service.telegram.AbstractHandler;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@SuppressWarnings("unused")
@Module
public abstract class AddAccountHandlersModule {
    @Binds
    @IntoSet
    abstract AbstractHandler addAccountHandler(AddAccountHandler handler);
}
