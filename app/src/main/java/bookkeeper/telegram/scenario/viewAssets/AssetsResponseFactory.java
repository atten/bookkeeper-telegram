package bookkeeper.telegram.scenario.viewAssets;

import bookkeeper.entity.TelegramUser;
import bookkeeper.service.repository.AccountRepository;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.service.repository.AccountTransferRepository;
import bookkeeper.service.repository.ExchangeRateRepository;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

class AssetsResponseFactory {
    private final AccountRepository accountRepository;
    private final AccountTransactionRepository transactionRepository;
    private final AccountTransferRepository transferRepository;
    private final ExchangeRateRepository exchangeRateRepository;

    @Inject
    AssetsResponseFactory(AccountRepository accountRepository, AccountTransactionRepository transactionRepository, AccountTransferRepository transferRepository, ExchangeRateRepository exchangeRateRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transferRepository = transferRepository;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    String getTotalAssets(TelegramUser user, int monthOffset) {
        var dateVerbose = LocalDate.now().plusMonths(monthOffset).format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        var exchangeCurrency = Currency.getInstance("RUB");
        var exchangeRateCache = new HashMap<Currency, BigDecimal>();

        var assets = accountRepository.filter(user).stream()
            .map(account ->
                new Asset(
                    account,
                    BigDecimal.ZERO
                        .add(transactionRepository.getTransactionBalance(account, monthOffset))
                        .add(transferRepository.getTransferBalance(account, monthOffset)),
                    exchangeRateCache.computeIfAbsent(account.getCurrency(), currency -> exchangeRateRepository.getExchangeRate(currency, exchangeCurrency).orElse(BigDecimal.ZERO)),
                    exchangeCurrency
                )
            ).toList();

        var netAssets = assets.stream().map(Asset::getExchangeBalance).reduce(BigDecimal.ZERO, BigDecimal::add).floatValue();

        var content = assets
            .stream()
            .sorted(Comparator.comparing(i -> i.getExchangeBalance().negate())) // descending order
            .map(asset ->
                String.format(
                    "%-15.15s: %15.15s: %5.5s",
                    asset.account().getName(),
                    String.format("% ,.2f %s", asset.balance(), asset.account().getCurrency().getSymbol()),
                    String.format("%.1f%%", asset.getExchangeBalance().floatValue() / netAssets * 100)
                )
            )
            .collect(Collectors.joining("\n"));

        var summary = String.format("Итог: %,.2f %s", netAssets, exchangeCurrency.getSymbol());

        return String.format("Сводка по всем счетам на конец *%s*:\n```\n%s\n```%s\n", dateVerbose, content, summary);
    }
}
