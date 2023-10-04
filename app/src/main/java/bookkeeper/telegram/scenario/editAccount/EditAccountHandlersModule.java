package bookkeeper.telegram.scenario.editAccount;

import bookkeeper.service.telegram.AbstractHandler;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@SuppressWarnings("unused")
@Module
public abstract class EditAccountHandlersModule {
    @Binds
    @IntoSet
    abstract AbstractHandler listAccountsCallbackHandler(ListAccountsCallbackHandler handler);

    @Binds
    @IntoSet
    abstract AbstractHandler showAccountDetailsCallbackHandler(ShowAccountDetailsCallbackHandler handler);

    @Binds
    @IntoSet
    abstract AbstractHandler renameAccountCallbackHandler(RenameAccountCallbackHandler handler);

    @Binds
    @IntoSet
    abstract AbstractHandler setAccountNotesCallbackHandler(SetAccountNotesCallbackHandler handler);
}
