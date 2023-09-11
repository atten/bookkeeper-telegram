package bookkeeper.telegram;

import bookkeeper.telegram.shared.AbstractHandler;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@SuppressWarnings("unused")
@Module
abstract class CommonHandlersModule {
    @Binds
    @IntoSet
    abstract AbstractHandler localeHandler(LocaleHandler handler);

    @Binds
    @IntoSet
    abstract AbstractHandler loggingHandler(LoggingHandler handler);

    @Binds
    @IntoSet
    abstract AbstractHandler slashStartHandler(SlashStartHandler handler);

    @Binds
    @IntoSet
    abstract AbstractHandler unknownInputHandler(UnknownInputHandler handler);
}
