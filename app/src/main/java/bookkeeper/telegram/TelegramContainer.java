package bookkeeper.telegram;

import bookkeeper.telegram.scenario.addAccount.AddAccountHandlersModule;
import bookkeeper.telegram.scenario.addTransaction.freehand.FreehandHandlersModule;
import bookkeeper.telegram.scenario.addTransaction.tinkoff.TinkoffHandlersModule;
import bookkeeper.telegram.scenario.addTransfer.AddTransferHandlersModule;
import bookkeeper.telegram.scenario.editAccount.EditAccountHandlersModule;
import bookkeeper.telegram.scenario.editTransaction.EditTransactionHandlersModule;
import bookkeeper.telegram.scenario.searchTransactions.SearchTransactionsHandlersModule;
import bookkeeper.telegram.scenario.viewAssets.ViewAssetsHandlerModule;
import bookkeeper.telegram.scenario.viewMonthlyExpenses.ViewMonthlyExpensesHandlersModule;
import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(
    modules = {
        Config.class,
        EditTransactionHandlersModule.class,
        EditAccountHandlersModule.class,
        ViewAssetsHandlerModule.class,
        ViewMonthlyExpensesHandlersModule.class,
        AddTransferHandlersModule.class,
        FreehandHandlersModule.class,
        TinkoffHandlersModule.class,
        AddAccountHandlersModule.class,
        CommonHandlersModule.class,
        SearchTransactionsHandlersModule.class,
    }
)
interface TelegramContainer {
    Bot bot();
}
