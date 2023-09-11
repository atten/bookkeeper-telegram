package bookkeeper.telegram.scenario.viewAssets;

import bookkeeper.telegram.shared.AbstractHandler;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@SuppressWarnings("unused")
@Module
public abstract class ViewAssetsHandlerModule {
    @Binds
    @IntoSet
    abstract AbstractHandler viewAssetsHandler(ViewAssetsHandler handler);
}
