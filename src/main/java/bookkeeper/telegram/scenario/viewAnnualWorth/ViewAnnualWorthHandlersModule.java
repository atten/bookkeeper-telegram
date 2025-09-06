package bookkeeper.telegram.scenario.viewAnnualWorth;

import bookkeeper.service.telegram.AbstractHandler;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@SuppressWarnings("unused")
@Module
public abstract class ViewAnnualWorthHandlersModule {
    @Binds
    @IntoSet
    abstract AbstractHandler viewAnnualWorthHandler(ViewAnnualWorthHandler handler);
}
