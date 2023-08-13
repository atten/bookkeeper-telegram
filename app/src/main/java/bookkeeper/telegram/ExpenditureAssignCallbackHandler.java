package bookkeeper.telegram;

import bookkeeper.enums.Expenditure;
import bookkeeper.repositories.AccountTransactionRepository;
import bookkeeper.repositories.MerchantExpenditureRepository;
import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.services.parsers.Spending;
import bookkeeper.services.registries.SpendingParserRegistry;
import bookkeeper.services.registries.factories.SpendingParserRegistryFactoryAll;
import bookkeeper.telegram.callbacks.ExpenditureAssignCallback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.text.ParseException;

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

        // step 1
        transactionRepository.associateExpenditure(transaction, newExpenditure);
        editMessage(update, getResponseMessage(transaction), getResponseKeyboard(transaction));

        // step 2
        if (useAssociationFurther) {
            // restore spending from transaction raw message
            Spending spending;
            try {
                spending = spendingParserRegistry.parse(transaction.getRaw());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            merchantExpenditureRepository.addMerchantAssociation(spending.getMerchant(), newExpenditure, getTelegramUser(update));
            sendMessage(update, String.format("Категория '%s' будет использоваться по умолчанию для последующих записей '%s'.", newExpenditure.getName(), spending.getMerchant()));
        }

        return true;
    }


}
