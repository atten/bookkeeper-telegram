package bookkeeper.telegram.scenario.checkResidues;

import bookkeeper.service.telegram.AbstractHandler;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@SuppressWarnings("unused")
@Module
public abstract class CheckResiduesHandlersModule {
    @Binds
    @IntoSet
    abstract AbstractHandler checkResiduesMessageHandler(CheckResiduesMessageHandler handler);
}
