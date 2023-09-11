package bookkeeper.telegram.scenario.viewAssets;

import bookkeeper.entity.TelegramUser;
import bookkeeper.service.repository.AccountRepository;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.service.repository.AccountTransferRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    String getTotalAssets(TelegramUser user, int monthOffset) {
        var dateVerbose = LocalDate.now().plusMonths(monthOffset).format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        var content = accountRepository.filter(user).stream()
                .map(i -> new Asset(
                    i,
                    BigDecimal.ZERO
                        .add(transactionRepository.getTransactionBalance(i, monthOffset))
                        .add(transferRepository.getTransferBalance(i, monthOffset)))
                )
                .sorted(Comparator.comparing(i -> i.getBalance().negate()))
                .map(i -> String.format("%-15.15s: % ,.2f %s", i.getAccount().getName(), i.getBalance(), i.getAccount().getCurrency().getSymbol()))
                .collect(Collectors.joining("\n"));
        return String.format("Сводка по всем счетам на конец *%s*:\n```\n%s\n```", dateVerbose, content);
    }
}
