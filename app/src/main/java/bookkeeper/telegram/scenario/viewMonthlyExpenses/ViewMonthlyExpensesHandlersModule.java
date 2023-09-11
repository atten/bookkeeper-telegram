package bookkeeper.telegram.scenario.viewMonthlyExpenses;

import bookkeeper.telegram.shared.AbstractHandler;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@SuppressWarnings("unused")
@Module
public abstract class ViewMonthlyExpensesHandlersModule {
    @Binds
    @IntoSet
    abstract AbstractHandler selectMonthlyExpendituresCallbackHandler(SelectMonthlyExpendituresCallbackHandler handler);

    @Binds
    @IntoSet
    abstract AbstractHandler viewMonthlyExpensesHandler(ViewMonthlyExpensesHandler handler);
}
