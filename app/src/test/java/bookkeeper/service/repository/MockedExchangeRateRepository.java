package bookkeeper.service.repository;

import dagger.Reusable;
import jakarta.persistence.EntityManager;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Reusable
public class MockedExchangeRateRepository extends ExchangeRateRepository {
    @Inject
    public MockedExchangeRateRepository(EntityManager manager) {
        super(manager);
    }

    /**
     * Dynamically generate exchange rates in tests instead of fetching them from DB.
     * No missing values = no need to access external api to backfill them.
     */
    @Override
    public Map<Currency, BigDecimal> getExchangeRates(Set<Currency> currencies, Currency exchangeCurrency, LocalDate date) {
        var map = new HashMap<Currency, BigDecimal>();
        currencies.forEach(currency -> map.put(currency, new BigDecimal("50")));
        return map;
    }
}
