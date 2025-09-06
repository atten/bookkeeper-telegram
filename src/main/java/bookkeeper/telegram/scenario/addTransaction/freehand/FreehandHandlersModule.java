package bookkeeper.telegram.scenario.addTransaction.freehand;

import bookkeeper.service.telegram.AbstractHandler;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@SuppressWarnings("unused")
@Module
public abstract class FreehandHandlersModule {
    @Binds
    @IntoSet
    abstract AbstractHandler freehandRecordHandler(FreehandRecordHandler handler);
}
