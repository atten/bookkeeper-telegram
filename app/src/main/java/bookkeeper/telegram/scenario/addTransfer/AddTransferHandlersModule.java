package bookkeeper.telegram.scenario.addTransfer;

import bookkeeper.telegram.shared.AbstractHandler;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@SuppressWarnings("unused")
@Module
public abstract class AddTransferHandlersModule {
    @Binds
    @IntoSet
    abstract AbstractHandler addTransferHandler(AddTransferHandler handler);
}
