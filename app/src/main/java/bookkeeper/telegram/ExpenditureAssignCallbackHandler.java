package bookkeeper.telegram;

import bookkeeper.enums.Expenditure;
import bookkeeper.repositories.AccountTransactionRepository;
import bookkeeper.repositories.MerchantExpenditureRepository;
import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.services.parsers.Spending;
import bookkeeper.services.registries.SpendingParserRegistry;
import bookkeeper.services.registries.factories.SpendingParserRegistryFactoryAll;
import bookkeeper.telegram.callbacks.ExpenditureAssignCallback;
import bookkeeper.telegram.callbacks.MerchantExpenditureRemoveCallback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.text.ParseException;
import java.util.stream.Collectors;

import static bookkeeper.telegram.responses.TransactionResponseFactory.getResponseKeyboard;
import static bookkeeper.telegram.responses.TransactionResponseFactory.getResponseMessage;


/**
 * Scenario: user assigns transaction expenditure.
 */
public class ExpenditureAssignCallbackHandler extends AbstractHandler {
    private final AccountTransactionRepository transactionRepository;
    private final MerchantExpenditureRepository merchantExpenditureRepository;
    private final SpendingParserRegistry spendingParserRegistry = SpendingParserRegistryFactoryAll.create();

    ExpenditureAssignCallbackHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountTransactionRepository transactionRepository, MerchantExpenditureRepository merchantExpenditureRepository) {
        super(bot, telegramUserRepository);
        this.transactionRepository = transactionRepository;
        this.merchantExpenditureRepository = merchantExpenditureRepository;
    }

    /**
     * Handle expenditure selector click:
     * 1. Associate specified AccountTransaction with given Expenditure.
     * 2. Store association with given merchant for further transactions (if applicable).
     */
    @Override
    Boolean handle(Update update) {
        var callbackMessage = getCallbackMessage(update);
        if (!(callbackMessage instanceof ExpenditureAssignCallback))
            return false;

        var cm = ((ExpenditureAssignCallback) callbackMessage);
        var transaction = transactionRepository.get(cm.getTransactionId());
        var previousExpenditure = transaction.getExpenditure();
        var newExpenditure = cm.getExpenditure();
        var useAssociationFurther = previousExpenditure == Expenditure.OTHER && newExpenditure != Expenditure.OTHER;
        var pendingTransactionsCount = cm.getPendingTransactionIds().size();

        // step 1
        transactionRepository.associateExpenditure(transaction, newExpenditure);

        if (pendingTransactionsCount == 0) {
            editMessage(update, getResponseMessage(transaction), getResponseKeyboard(transaction));
        } else {
            var nextPendingTransaction = transactionRepository.get(cm.getPendingTransactionIds().get(0));
            var remainingTransactionIds = cm.getPendingTransactionIds().stream().skip(1).collect(Collectors.toList());
            editMessage(update, getResponseMessage(nextPendingTransaction, remainingTransactionIds.size()), getResponseKeyboard(nextPendingTransaction, remainingTransactionIds));
        }

        // step 2
        if (useAssociationFurther) {
            // restore spending from transaction raw message
            Spending spending;
            try {
                spending = spendingParserRegistry.parse(transaction.getRaw());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            var merchant = spending.getMerchant();
            merchantExpenditureRepository.addMerchantAssociation(merchant, newExpenditure, getTelegramUser(update));
            var message = String.format("Категория *%s* будет использоваться по умолчанию для последующих записей `%s`.", newExpenditure.getName(), merchant);
            var keyboard = new InlineKeyboardMarkup().addRow(new MerchantExpenditureRemoveCallback(merchant, newExpenditure).asButton("Отмена"));
            sendMessage(update, message, keyboard);
        }

        return true;
    }


}
