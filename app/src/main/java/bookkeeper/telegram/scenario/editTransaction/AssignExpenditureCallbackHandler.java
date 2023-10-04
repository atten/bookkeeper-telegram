package bookkeeper.telegram.scenario.editTransaction;

import bookkeeper.entity.AccountTransaction;
import bookkeeper.enums.Expenditure;
import bookkeeper.service.parser.Spending;
import bookkeeper.service.parser.SpendingParserRegistry;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.service.repository.MerchantExpenditureRepository;
import bookkeeper.service.telegram.AbstractHandler;
import bookkeeper.service.telegram.Request;
import bookkeeper.exception.AccountTransactionNotFound;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.List;

import static bookkeeper.telegram.scenario.editTransaction.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.scenario.editTransaction.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user assigns transaction expenditure.
 */
class AssignExpenditureCallbackHandler implements AbstractHandler {
    private final AccountTransactionRepository transactionRepository;
    private final MerchantExpenditureRepository merchantExpenditureRepository;
    private final SpendingParserRegistry spendingParserRegistry = SpendingParserRegistry.ofAllParsers();

    @Inject
    AssignExpenditureCallbackHandler(AccountTransactionRepository transactionRepository, MerchantExpenditureRepository merchantExpenditureRepository) {
        this.transactionRepository = transactionRepository;
        this.merchantExpenditureRepository = merchantExpenditureRepository;
    }

    /**
     * Handle expenditure selector click:
     * 1. Store association with given merchant for further transactions (if applicable).
     * 2. Associate specified AccountTransaction with given Expenditure.
     * 3. Associate pending AccountTransactions with same merchant too (if any).
     */
    public Boolean handle(Request request) throws AccountTransactionNotFound {
        if (!(request.getCallbackMessage().orElse(null) instanceof AssignExpenditureCallback cm))
            return false;

        var transaction = transactionRepository.get(cm.getTransactionId()).orElseThrow(() -> new AccountTransactionNotFound(cm.getTransactionId()));
        var merchant = getSpendingFromTransaction(transaction).getMerchant();
        var newExpenditure = cm.getExpenditure();
        var hasAssociation = merchantExpenditureRepository.find(merchant, request.getTelegramUser()).isPresent();
        var useAssociationFurther = newExpenditure != Expenditure.OTHER && !hasAssociation;
        var pendingTransactionsCount = cm.getPendingTransactionIds().size();

        // step 1
        if (useAssociationFurther) {
            merchantExpenditureRepository.addMerchantAssociation(merchant, newExpenditure, request.getTelegramUser());
            var keyboard = new InlineKeyboardMarkup().addRow(new RemoveMerchantExpenditureCallback(merchant, newExpenditure).asButton("Отмена"));
            request.sendMessage(getResponseMessage(merchant, newExpenditure), keyboard);
        }

        // step 2
        transaction.setExpenditure(newExpenditure);

        if (pendingTransactionsCount == 0) {
            request.editMessage(getResponseMessage(transaction), getResponseKeyboard(transaction));
        } else {
            var pendingTransactions = transactionRepository.findByIds(cm.getPendingTransactionIds());

            if (useAssociationFurther) {
                // step 3
                updateTransactionsExpenditure(pendingTransactions, merchant, newExpenditure);
            }

            request.editMessage(getResponseMessage(transaction, pendingTransactionsCount), getResponseKeyboard(transaction, cm.getPendingTransactionIds()));
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
                transaction.setExpenditure(newExpenditure);
            }
        }
    }
}
