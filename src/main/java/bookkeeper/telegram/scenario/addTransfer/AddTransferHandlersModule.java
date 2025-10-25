package bookkeeper.telegram.scenario.addTransfer;

import bookkeeper.service.telegram.AbstractHandler;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@SuppressWarnings("unused")
@Module
public abstract class AddTransferHandlersModule {
    @Binds
    @IntoSet
    abstract AbstractHandler addTransferHandler(AddTransferCallbackHandler handler);

    @Binds
    @IntoSet
    abstract AbstractHandler removeTransferHandler(RemoveTransferCallbackHandler handler);
}
