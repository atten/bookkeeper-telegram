package bookkeeper.dao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Currency;

@Entity
@Table(name = "accounts", uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "currency", "telegram_user" }) })
public class Account {
    @Id
    @GeneratedValue
    @Getter
    private long id;

    @Column(nullable = false)
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String notes;

    @Column(nullable = false)
    private String currency;

    @Getter
    @Setter
    @Column(nullable = false, name = "is_hidden")
    private boolean isHidden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "telegram_user")
    @Getter
    @Setter
    private TelegramUser telegramUser;

    @Column(nullable = false, name = "created_at", updatable = false)
    @Getter
    @Setter
    private Instant createdAt;

    public boolean isVisible() { return !isHidden; }

    public Currency getCurrency() {
        return Currency.getInstance(currency);
    }

    public void setCurrency(Currency currency) {
        this.currency = currency.getCurrencyCode();
    }
}
