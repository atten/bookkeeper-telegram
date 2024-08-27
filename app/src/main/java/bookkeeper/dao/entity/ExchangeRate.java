package bookkeeper.dao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;

/**
 * 1 'singularCurrency' = 'price' 'exchangeCurrency'
 * Example: 1 USD = 99 RUB
 */
@Entity
@Table(name = "exchange_rates")
public class ExchangeRate {
    @Id
    @GeneratedValue
    @Getter
    private long id;

    @Column(nullable = false, name = "singular_currency")
    private String singularCurrency;

    @Column(nullable = false, name = "exchange_currency")
    private String exchangeCurrency;

    @Column(nullable = false)
    @Getter
    @Setter
    private BigDecimal price;

    /**
     * Measurement moment
     */
    @Column(nullable = false)
    @Getter
    @Setter
    private Instant timestamp;

    public Currency getSingularCurrency() {
        return Currency.getInstance(singularCurrency);
    }

    public void setSingularCurrency(Currency currency) {
        this.singularCurrency = currency.getCurrencyCode();
    }

    public Currency getExchangeCurrency() {
        return Currency.getInstance(exchangeCurrency);
    }

    public void setExchangeCurrency(Currency currency) {
        this.exchangeCurrency = currency.getCurrencyCode();
    }
}
