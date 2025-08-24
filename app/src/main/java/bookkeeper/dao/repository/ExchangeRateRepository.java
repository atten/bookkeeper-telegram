package bookkeeper.dao.repository;

import bookkeeper.dao.entity.ExchangeRate;
import dagger.Reusable;
import jakarta.persistence.EntityManager;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Reusable
public class ExchangeRateRepository {
    private final EntityManager manager;

    @Inject
    ExchangeRateRepository(EntityManager manager) {
        this.manager = manager;
    }

    public Map<Currency, BigDecimal> getExchangeRates(Set<Currency> currencies, Currency exchangeCurrency, LocalDate date) {
        var rates = new HashMap<Currency, BigDecimal>();

        // handle 1 RUB = 1 RUB case
        if (currencies.contains(exchangeCurrency)) {
            rates.put(exchangeCurrency, BigDecimal.ONE);
        }

        var currenciesFilter = Stream.concat(
            currencies.stream(),
            Stream.of(exchangeCurrency)
        )
            .map(Currency::getCurrencyCode)
            .collect(Collectors.toSet());

        var sql = "SELECT * FROM exchange_rates " +
                "WHERE singular_currency IN :currenciesFilter " +
                "AND exchange_currency IN :currenciesFilter " +
                "AND DateTime::MakeDate(timestamp) = :date";

        var query = manager.createNativeQuery(sql, ExchangeRate.class)
            .setParameter("currenciesFilter", currenciesFilter)
            .setParameter("date", date);

        for (var row : query.getResultList()) {
            var exchangeRate = (ExchangeRate)row;
            if (exchangeRate.getExchangeCurrency().equals(exchangeCurrency)) {
                rates.put(exchangeRate.getSingularCurrency(), exchangeRate.getPrice());
            }
        }

        return rates;
    }

    public void backfillExchangeRates(Map<Currency, BigDecimal> rates, Currency exchangeCurrency, LocalDate date) {
        var entities = rates
            .keySet()
            .stream()
            .map(currency -> exchangeRateFactory(currency, exchangeCurrency, rates.get(currency), date))
            .toList();
        entities.forEach(manager::persist);
    }

    private ExchangeRate exchangeRateFactory(Currency singularCurrency, Currency exchangeCurrency, BigDecimal price, LocalDate date) {
        var obj = new ExchangeRate();
        obj.setSingularCurrency(singularCurrency);
        obj.setExchangeCurrency(exchangeCurrency);
        obj.setPrice(price);
        obj.setTimestamp(date.atTime(12, 0).toInstant(ZoneOffset.UTC));
        return obj;
    }
}
