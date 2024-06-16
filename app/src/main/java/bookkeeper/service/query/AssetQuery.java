package bookkeeper.service.query;

import bookkeeper.entity.Account;
import bookkeeper.entity.TelegramUser;
import bookkeeper.service.client.CbrApiClient;
import bookkeeper.service.repository.AccountRepository;
import bookkeeper.service.repository.AccountTransactionRepository;
import bookkeeper.service.repository.AccountTransferRepository;
import bookkeeper.service.repository.ExchangeRateRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class AssetQuery {
    @Getter
    private final Currency exchangeCurrency = Currency.getInstance("RUB");

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository transactionRepository;
    private final AccountTransferRepository transferRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final CbrApiClient apiClient;

    @Inject
    public AssetQuery(AccountRepository accountRepository, AccountTransactionRepository transactionRepository, AccountTransferRepository transferRepository, ExchangeRateRepository exchangeRateRepository, CbrApiClient apiClient) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transferRepository = transferRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.apiClient = apiClient;
    }

    public static float getNetAssetsValue(List<Asset> assets) {
        return assets.stream().map(Asset::getExchangeBalance).reduce(BigDecimal.ZERO, BigDecimal::add).floatValue();
    }

    public List<Asset> getMonthlyAssets(TelegramUser user, int monthOffset) {
        var exchangeDate = getExchangeDate(monthOffset);
        var exchangeRates = getExchangeRates(user, exchangeDate);

        return accountRepository.filter(user).stream()
            .map(account ->
                new Asset(
                    account,
                    BigDecimal.ZERO
                        .add(transactionRepository.getTransactionBalance(account, monthOffset))
                        .add(transferRepository.getTransferBalance(account, monthOffset)),
                    exchangeRates.getOrDefault(account.getCurrency(), BigDecimal.ZERO),
                    exchangeCurrency
                )
            )
            .filter(asset -> !asset.isEmpty())
            .sorted(Comparator.comparing(i -> i.getExchangeBalance().negate())) // descending order
            .toList();
    }

    public static LocalDate getExchangeDate(int monthOffset) {
        return monthOffset >= 0 ?
            LocalDate.now() :
            // last day of month = first day of next month - 1 day
            LocalDate.now().plusMonths(monthOffset + 1).withDayOfMonth(1).minusDays(1);
    }

    public Map<Currency, BigDecimal> getExchangeRates(TelegramUser user, LocalDate exchangeDate) {
        var accounts = accountRepository.filter(user);
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
        return exchangeRates;
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

    public record Asset(Account account, BigDecimal balance, BigDecimal exchangeRate, Currency exchangeCurrency) {
        public BigDecimal getExchangeBalance() {
            return balance.multiply(exchangeRate);
        }

        boolean isEmpty() {
            return balance.stripTrailingZeros().equals(BigDecimal.ZERO);
        }
    }
}
