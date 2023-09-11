package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.telegram.shared.AbstractHandler;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@SuppressWarnings("unused")
@Module
public abstract class EditTransactionHandlersModule {
    @Binds
    @IntoSet
    abstract AbstractHandler approveTransactionBulkCallbackHandler(ApproveTransactionBulkCallbackHandler handler);

    @Binds
    @IntoSet
    abstract AbstractHandler approveTransactionCallbackHandler(ApproveTransactionCallbackHandler handler);

    @Binds
    @IntoSet
    abstract AbstractHandler assignExpenditureCallbackHandler(AssignExpenditureCallbackHandler handler);

    @Binds
    @IntoSet
    abstract AbstractHandler editTransactionBulkCallbackHandler(EditTransactionBulkCallbackHandler handler);

    @Binds
    @IntoSet
    abstract AbstractHandler removeMerchantExpenditureCallbackHandler(RemoveMerchantExpenditureCallbackHandler handler);

    @Binds
    @IntoSet
    abstract AbstractHandler removeTransactionCallbackHandler(RemoveTransactionCallbackHandler handler);

    @Binds
    @IntoSet
    abstract AbstractHandler selectExpenditureCallbackHandler(SelectExpenditureCallbackHandler handler);

    @Binds
    @IntoSet
    abstract AbstractHandler shiftTransactionMonthCallbackHandler(ShiftTransactionMonthCallbackHandler handler);

    @Binds
    @IntoSet
    abstract AbstractHandler slashClearAssociationsHandler(SlashClearAssociationsHandler handler);
}
