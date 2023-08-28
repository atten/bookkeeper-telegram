package bookkeeper.telegram.scenarios.viewAssets;

import bookkeeper.entities.TelegramUser;
import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.repositories.AccountTransactionRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

class AssetsResponseFactory {
    private final AccountRepository accountRepository;
    private final AccountTransactionRepository transactionRepository;

    AssetsResponseFactory(AccountRepository accountRepository, AccountTransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    String getTotalAssets(TelegramUser user) {
        var accounts = accountRepository.find(user);
        var contendWithSortingKey = new HashMap<String, BigDecimal>();

        for (var account : accounts) {
            var name = account.getName();
            var balance = transactionRepository.getTotalAmount(account);
            var currency = account.getCurrency().getSymbol();

            var line = String.format("%-15s: % ,.2f %s", name, balance, currency);
            var sortingKey = balance.negate();
            contendWithSortingKey.put(line, sortingKey);
        }

        var content = contendWithSortingKey.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.joining("\n"));
        return "Сводка по всем счетам:```\n" + content + "```";
    }

}
