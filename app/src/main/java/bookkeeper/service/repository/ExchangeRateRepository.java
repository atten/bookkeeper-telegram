package bookkeeper.service.repository;

import bookkeeper.entity.ExchangeRate;
import dagger.Reusable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;
import java.util.stream.Stream;

@Reusable
public class ExchangeRateRepository {
    private final EntityManager manager;

    @Inject
    ExchangeRateRepository(EntityManager manager) {
        this.manager = manager;
    }

    public Optional<BigDecimal> getExchangeRate(Currency singularCurrency, Currency exchangeCurrency) {
        if (singularCurrency.equals(exchangeCurrency))
            return Optional.of(BigDecimal.ONE);

        var currencies = Stream.of(singularCurrency, exchangeCurrency).map(Currency::getCurrencyCode).toList();

        var sql = "SELECT i from ExchangeRate i " +
                "WHERE i.singularCurrency IN :currencies " +
                "AND i.exchangeCurrency IN :currencies " +
                "AND date_trunc('day', timestamp) = date_trunc('day', current_timestamp) " +
                "ORDER BY i.timestamp DESC LIMIT 1";

        var query = manager.createQuery(sql, ExchangeRate.class)
            .setParameter("currencies", currencies);

        try {
            return Optional.of(query.getSingleResult().getPrice());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
