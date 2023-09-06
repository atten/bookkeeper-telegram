package bookkeeper.entities;

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

    @Getter
    @Setter
    private String name;

    private String currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "telegram_user")
    @Getter
    @Setter
    private TelegramUser telegramUser;

    @Column(nullable = false, name = "created_at")
    @Getter
    @Setter
    private Instant createdAt;

    public Currency getCurrency() {
        return Currency.getInstance(currency);
    }

    public void setCurrency(Currency currency) {
        this.currency = currency.getCurrencyCode();
    }
}
