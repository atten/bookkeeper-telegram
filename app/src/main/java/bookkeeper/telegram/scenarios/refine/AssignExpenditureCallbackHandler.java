package bookkeeper.telegram.scenarios.refine;

import bookkeeper.entities.AccountTransaction;
import bookkeeper.enums.Expenditure;
import bookkeeper.repositories.AccountTransactionRepository;
import bookkeeper.repositories.MerchantExpenditureRepository;
import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.services.parsers.Spending;
import bookkeeper.services.registries.SpendingParserRegistry;
import bookkeeper.services.registries.factories.SpendingParserRegistryFactoryAll;
import bookkeeper.telegram.shared.AbstractHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user assigns transaction expenditure.
 */
public class AssignExpenditureCallbackHandler extends AbstractHandler {
    private final AccountTransactionRepository transactionRepository;
    private final MerchantExpenditureRepository merchantExpenditureRepository;
    private final SpendingParserRegistry spendingParserRegistry = SpendingParserRegistryFactoryAll.create();

    public AssignExpenditureCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountTransactionRepository transactionRepository, MerchantExpenditureRepository merchantExpenditureRepository) {
        super(bot, telegramUserRepository);
        this.transactionRepository = transactionRepository;
        this.merchantExpenditureRepository = merchantExpenditureRepository;
    }

    /**
     * Handle expenditure selector click:
     * 1. Associate specified AccountTransaction with given Expenditure.
     * 2. Associate pending AccountTransactions with same merchant too (if any).
     * 3. Store association with given merchant for further transactions (if applicable).
     */
    @Override
    public Boolean handle(Update update) {
        var callbackMessage = getCallbackMessage(update);
        if (!(callbackMessage instanceof AssignExpenditureCallback))
            return false;

        var cm = ((AssignExpenditureCallback) callbackMessage);
        var transaction = transactionRepository.get(cm.getTransactionId());
        var merchant = getSpendingFromTransaction(transaction).getMerchant();
        var previousExpenditure = transaction.getExpenditure();
        var newExpenditure = cm.getExpenditure();
        var useAssociationFurther = previousExpenditure == Expenditure.OTHER && newExpenditure != Expenditure.OTHER;
        var pendingTransactionsCount = cm.getPendingTransactionIds().size();

        // step 1
        transactionRepository.associateExpenditure(transaction, newExpenditure);

        if (pendingTransactionsCount == 0) {
            editMessage(update, getResponseMessage(transaction), getResponseKeyboard(transaction));
        } else if (useAssociationFurther) {
            // step 2
            var pendingTransactions = transactionRepository.getList(cm.getPendingTransactionIds());
            updateTransactionsExpenditure(pendingTransactions, merchant, newExpenditure);

            var nextPendingTransaction = pendingTransactions.get(0);
            var remainingTransactionIds = cm.getPendingTransactionIds().stream().skip(1).collect(Collectors.toList());
            editMessage(update, getResponseMessage(nextPendingTransaction, remainingTransactionIds.size()), getResponseKeyboard(nextPendingTransaction, remainingTransactionIds));
        }

        // step 2
        if (useAssociationFurther) {
            merchantExpenditureRepository.addMerchantAssociation(merchant, newExpenditure, getTelegramUser(update));
            var keyboard = new InlineKeyboardMarkup().addRow(new MerchantExpenditureRemoveCallback(merchant, newExpenditure).asButton("Отмена"));
            sendMessage(update, getResponseMessage(merchant, newExpenditure), keyboard);
        }

        return true;
    }

    /**
     * restore spending from transaction raw message
     */
    private Spending getSpendingFromTransaction(AccountTransaction transaction) {
        try {
            return spendingParserRegistry.parse(transaction.getRaw());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateTransactionsExpenditure(List<AccountTransaction> transactions, String merchantFilter, Expenditure newExpenditure) {
        for (var transaction : transactions) {
            var merchant = getSpendingFromTransaction(transaction).getMerchant();
            if (merchant.equals(merchantFilter)) {
                transactionRepository.associateExpenditure(transaction, newExpenditure);
            }
        }
    }
}
