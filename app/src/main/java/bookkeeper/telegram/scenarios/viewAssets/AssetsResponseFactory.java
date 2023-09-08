package bookkeeper.telegram.scenarios.viewAssets;

import bookkeeper.entities.TelegramUser;
import bookkeeper.services.repositories.AccountRepository;
import bookkeeper.services.repositories.AccountTransactionRepository;
import bookkeeper.services.repositories.AccountTransferRepository;

import java.util.*;
import java.util.stream.Collectors;

class AssetsResponseFactory {
    private final AccountRepository accountRepository;
    private final AccountTransactionRepository transactionRepository;
    private final AccountTransferRepository transferRepository;

    AssetsResponseFactory(AccountRepository accountRepository, AccountTransactionRepository transactionRepository, AccountTransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transferRepository = transferRepository;
    }

    String getTotalAssets(TelegramUser user) {
        var content = accountRepository.filter(user).stream()
                .map(i -> new Asset(i, transactionRepository.getTransactionBalance(i).add(transferRepository.getTransferBalance(i))))
                .sorted(Comparator.comparing(i -> i.getBalance().negate()))
                .map(i -> String.format("%-15s: % ,.2f %s", i.getAccount().getName(), i.getBalance(), i.getAccount().getCurrency().getSymbol()))
                .collect(Collectors.joining("\n"));
        return "Сводка по всем счетам:\n```\n" + content + "\n```";
    }

}
