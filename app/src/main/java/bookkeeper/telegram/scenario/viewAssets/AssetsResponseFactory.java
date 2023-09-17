package bookkeeper.telegram.scenario.viewAssets;

import bookkeeper.entity.Account;
import bookkeeper.entity.TelegramUser;
import bookkeeper.service.client.CbrApiClient;
import bookkeeper.service.repository.AccountRepository;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.service.repository.AccountTransferRepository;
import bookkeeper.service.repository.ExchangeRateRepository;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
class AssetsResponseFactory {
    private final AccountRepository accountRepository;
    private final AccountTransactionRepository transactionRepository;
    private final AccountTransferRepository transferRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final CbrApiClient apiClient;

    @Inject
    AssetsResponseFactory(AccountRepository accountRepository, AccountTransactionRepository transactionRepository, AccountTransferRepository transferRepository, ExchangeRateRepository exchangeRateRepository, CbrApiClient apiClient) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transferRepository = transferRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.apiClient = apiClient;
    }

    String getTotalAssets(TelegramUser user, int monthOffset) {
        var exchangeDate = monthOffset >= 0 ?
                LocalDate.now() :
                // last day of month = first day of next month - 1 day
                LocalDate.now().plusMonths(monthOffset + 1).withDayOfMonth(1).minusDays(1);
        var accounts = accountRepository.filter(user);
        var exchangeCurrency = Currency.getInstance("RUB");
        var currencies = accounts.stream().map(Account::getCurrency).collect(Collectors.toSet());
        var exchangeRates = exchangeRateRepository.getExchangeRates(currencies, exchangeCurrency, exchangeDate);
        var missingRates = currencies.stream().filter(currency -> !exchangeRates.containsKey(currency)).collect(Collectors.toSet());

        log.info(String.format("Exchange date: %s", exchangeDate));
        log.info(String.format("Required exchange rates: %s", currencies));
        log.info(String.format("Existing exchange rates: %s", exchangeRates));
        log.info(String.format("Missing  exchange rates: %s", missingRates));

        if (!missingRates.isEmpty()) {
            try {
                log.info("Backfill exchange rates...");
                var backfilledRates = backfillMissingExchangeRates(missingRates, exchangeCurrency, exchangeDate);
                exchangeRates.putAll(backfilledRates);
            } catch (IOException e) {
                log.error(e.toString());
            } finally {
                log.info(String.format("Complete exchange rates: %s", exchangeRates));
            }
        }

        var assets = accountRepository.filter(user).stream()
            .map(account ->
                new Asset(
                    account,
                    BigDecimal.ZERO
                        .add(transactionRepository.getTransactionBalance(account, monthOffset))
                        .add(transferRepository.getTransferBalance(account, monthOffset)),
                    exchangeRates.getOrDefault(account.getCurrency(), BigDecimal.ZERO),
                    exchangeCurrency
                )
            ).toList();

        var netAssets = assets.stream().map(Asset::getExchangeBalance).reduce(BigDecimal.ZERO, BigDecimal::add).floatValue();

        var nbsp = "\u00A0";
        var content = assets
            .stream()
            .filter(asset -> !asset.isEmpty())
            .sorted(Comparator.comparing(i -> i.getExchangeBalance().negate())) // descending order
            .map(asset ->
                String.format(
                    "%-15.15s %15.15s | %5.5s".replace(" | ", nbsp + "|" + nbsp),
                    asset.account().getName(),
                    String.format("% ,.2f %s", asset.balance(), asset.account().getCurrency().getSymbol()),
                    String.format("%.1f%%", asset.getExchangeBalance().floatValue() / netAssets * 100)
                )
            )
            .collect(Collectors.joining("\n"));

        var date = LocalDate.now().plusMonths(monthOffset);
        var monthShortVerbose = date.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());

        var result = new StringJoiner("\n\n");
        result
            .add(String.format("\uD83D\uDCD8 Сводка по непустым счетам на конец *%s*:", date.format(DateTimeFormatter.ofPattern("MMMM yyyy"))))
            .add(String.format("```\n%s```", content))
            .add(String.format("\uD83D\uDCC8 *Курс на %s*:\n%s", exchangeDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)), exchangeRatesVerbose(exchangeRates)))
            .add(String.format("\uD83C\uDFDB *Итог за %s*: %,.2f %s", monthShortVerbose, netAssets, exchangeCurrency.getSymbol()));
        return result.toString();
    }

    /**
     * Collect and backfill missing exchange rates.
     */
    private Map<Currency, BigDecimal> backfillMissingExchangeRates(Set<Currency> missing, Currency exchangeCurrency, LocalDate date) throws IOException {
        var responseMap = apiClient.getRubExchangeRates(date);
        var backfilledMap = responseMap
            .keySet()
            .stream()
            .filter(missing::contains)
            .collect(Collectors.toMap(currency -> currency, responseMap::get));
        exchangeRateRepository.backfillExchangeRates(backfilledMap, exchangeCurrency, date);
        return backfilledMap;
    }

    /**
     * Example: EUR 100, USD 99
     */
    private String exchangeRatesVerbose(Map<Currency, BigDecimal> rates) {
        return rates
            .entrySet()
            .stream()
            .filter(entry -> !entry.getValue().equals(BigDecimal.ONE))  // skip native currency
            .map(entry -> String.format("%s %s", entry.getKey().getCurrencyCode(), entry.getValue()))
            .collect(Collectors.joining(" | "));
    }
}
