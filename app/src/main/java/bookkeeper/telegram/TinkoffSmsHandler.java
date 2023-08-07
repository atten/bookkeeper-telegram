package bookkeeper.telegram;

import bookkeeper.entities.AccountTransaction;
import bookkeeper.entities.TelegramUser;
import bookkeeper.enums.Expenditure;
import bookkeeper.repositories.AccountRepository;
import bookkeeper.repositories.AccountTransactionRepository;
import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.services.matchers.shared.ExpenditureMatcherByMerchant;
import bookkeeper.services.registries.TransactionParserRegistry;
import bookkeeper.services.registries.factories.TransactionParserRegistryFactoryTinkoff;
import bookkeeper.telegram.callbacks.ExpenditurePickCallback;
import bookkeeper.telegram.callbacks.TransactionApproveCallback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Scenario: user stores transactions.
 */
public class TinkoffSmsHandler extends AbstractHandler {
    private final TransactionParserRegistry transactionParserRegistry;
    private final AccountTransactionRepository transactionRepository;

    TinkoffSmsHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository, AccountRepository accountRepository, AccountTransactionRepository transactionRepository, ExpenditureMatcherByMerchant expenditureMatcherByMerchant) {
        super(bot, telegramUserRepository);
        this.transactionParserRegistry = new TransactionParserRegistryFactoryTinkoff(accountRepository, expenditureMatcherByMerchant).create();
        this.transactionRepository = transactionRepository;
    }

    /**
     * Parse SMS text from Tinkoff and display summary.
     * Take Raw SMS list, Transform to AccountTransaction and put to AccountTransactionRepository.
     */
    @Override
    Boolean handle(Update update) {
        if (update.message() == null)
            return false;

        var smsList = update.message().text().split("\n");
        List<AccountTransaction> transactions;
        try {
            transactions = userSendsBankingMessages(smsList, getTelegramUser(update));
        } catch (ParseException e) {
            // provided sms was not parsed
            return false;
        }

        sendMessage(update, getResponseMessage(transactions), getResponseKeyboard(transactions));
        return true;

    }

    List<AccountTransaction> userSendsBankingMessages(String[] bankingMessages, TelegramUser user) throws ParseException {
        List<AccountTransaction> results = new ArrayList<>();

        for (var message : bankingMessages ) {
            var transaction = transactionParserRegistry.parse(message, user);
            transactionRepository.save(transaction);
            results.add(transaction);
        }

        return results;
    }

    private String getResponseMessage(List<AccountTransaction> transactions) {
        if (transactions.size() == 0) {
            return "Не добавлено ни одной записи";
        }

        // build counter for each expenditure
        Map<Expenditure, AtomicLong> counterMap = new HashMap<>();
        transactions.forEach(transaction -> {
            counterMap.putIfAbsent(transaction.getExpenditure(), new AtomicLong(0));
            counterMap.get(transaction.getExpenditure()).incrementAndGet();
        });

        var totalItemsVerbose = String.format(
            getTextPlural(transactions.size(), "Добавлена %s запись", "Добавлены %s записи", "Добавлено %s записей"),
            transactions.size()
        );

        String statsVerbose;

        if (counterMap.size() == 1) {
            var expenditure = transactions.get(0).getExpenditure();
            statsVerbose = String.format("в категории \"%s\"", expenditure.getName());
        } else {
            totalItemsVerbose = totalItemsVerbose + ": ";
            statsVerbose = counterMap
                .entrySet()
                .stream()
                .map(entry -> String.format("%s в \"%s\"", entry.getValue(), entry.getKey().getName()))
                .reduce((s, s2) -> s + ", " + s2).orElse("");
        }

        var totalAmount = transactions.stream().map(AccountTransaction::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        var account = transactions.get(0).getAccount();
        var accountVerbose = String.format("на счет %s", account.getName());
        var totalAccountVerbose = String.format("стоимостью %s %s", totalAmount, account.getCurrency().getSymbol());

        return totalItemsVerbose + " " + statsVerbose + " " + accountVerbose + " " + totalAccountVerbose + ".";
    }

    private InlineKeyboardMarkup getResponseKeyboard(List<AccountTransaction> transactions) {
        var kb = new InlineKeyboardMarkup();
        if (transactions.size() == 1) {
            var transaction = transactions.get(0);
            return kb.addRow(
                new ExpenditurePickCallback(transaction.getId()).asButton("Уточнить категорию"),
                new TransactionApproveCallback(transaction.getId()).asButton("Подтвердить")
            );
        }
        return kb.addRow(new InlineKeyboardButton("Уточнить категории"), new InlineKeyboardButton("Подтвердить все"));
    }
}
