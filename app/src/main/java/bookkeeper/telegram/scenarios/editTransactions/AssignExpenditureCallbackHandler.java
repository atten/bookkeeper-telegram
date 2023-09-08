package bookkeeper.telegram.scenarios.editTransactions;

import bookkeeper.entities.AccountTransaction;
import bookkeeper.enums.Expenditure;
import bookkeeper.services.repositories.AccountTransactionRepository;
import bookkeeper.services.repositories.MerchantExpenditureRepository;
import bookkeeper.services.repositories.TelegramUserRepository;
import bookkeeper.services.parsers.Spending;
import bookkeeper.services.parsers.SpendingParserRegistry;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.CallbackMessageRegistry;
import bookkeeper.telegram.shared.exceptions.AccountTransactionNotFound;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.text.ParseException;
import java.util.List;

import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.shared.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user assigns transaction expenditure.
 */
public class AssignExpenditureCallbackHandler extends AbstractHandler {
    private final AccountTransactionRepository transactionRepository;
    private final MerchantExpenditureRepository merchantExpenditureRepository;
    private final SpendingParserRegistry spendingParserRegistry = SpendingParserRegistry.ofAllParsers();

    public AssignExpenditureCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountTransactionRepository transactionRepository, MerchantExpenditureRepository merchantExpenditureRepository) {
        super(bot, telegramUserRepository);
        this.transactionRepository = transactionRepository;
        this.merchantExpenditureRepository = merchantExpenditureRepository;
    }

    /**
     * Handle expenditure selector click:
     * 1. Store association with given merchant for further transactions (if applicable).
     * 2. Associate specified AccountTransaction with given Expenditure.
     * 3. Associate pending AccountTransactions with same merchant too (if any).
     */
    @Override
    public Boolean handle(Update update) throws AccountTransactionNotFound {
        var callbackMessage = CallbackMessageRegistry.getCallbackMessage(update);
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof AssignExpenditureCallback cm))
            return false;

        var transaction = transactionRepository.get(cm.getTransactionId()).orElseThrow(() -> new AccountTransactionNotFound(cm.getTransactionId()));
        var merchant = getSpendingFromTransaction(transaction).getMerchant();
        var newExpenditure = cm.getExpenditure();
        var hasAssociation = merchantExpenditureRepository.find(merchant, getTelegramUser(update)).isPresent();
        var useAssociationFurther = newExpenditure != Expenditure.OTHER && !hasAssociation;
        var pendingTransactionsCount = cm.getPendingTransactionIds().size();

        // step 1
        if (useAssociationFurther) {
            merchantExpenditureRepository.addMerchantAssociation(merchant, newExpenditure, getTelegramUser(update));
            var keyboard = new InlineKeyboardMarkup().addRow(new RemoveMerchantExpenditureCallback(merchant, newExpenditure).asButton("Отмена"));
            editMessage(update, getResponseMessage(merchant, newExpenditure), keyboard);
        }

        // step 2
        transaction.setExpenditure(newExpenditure);

        if (pendingTransactionsCount == 0) {
            // if message was edited in the step 1, then send a new one, otherwise reuse existing
            if (useAssociationFurther)
                sendMessage(update, getResponseMessage(transaction), getResponseKeyboard(transaction));
            else
                editMessage(update, getResponseMessage(transaction), getResponseKeyboard(transaction));

        } else {
            var pendingTransactions = transactionRepository.findByIds(cm.getPendingTransactionIds());

            if (useAssociationFurther) {
                // step 3
                updateTransactionsExpenditure(pendingTransactions, merchant, newExpenditure);
            }

            if (useAssociationFurther) {
                sendMessage(update, getResponseMessage(transaction, pendingTransactionsCount), getResponseKeyboard(transaction, cm.getPendingTransactionIds()));
            } else {
                editMessage(update, getResponseMessage(transaction, pendingTransactionsCount), getResponseKeyboard(transaction, cm.getPendingTransactionIds()));
            }
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